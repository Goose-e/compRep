package com.example.companyReputationManagement.iservice.services.review;

import com.example.companyReputationManagement.dao.CompanyDao;
import com.example.companyReputationManagement.dao.CompanySourceUrlDao;
import com.example.companyReputationManagement.dao.ReviewDao;
import com.example.companyReputationManagement.dao.UserCompanyRolesDao;
import com.example.companyReputationManagement.dto.review.find.ReviewRequestDto;
import com.example.companyReputationManagement.dto.review.find.ReviewResponse;
import com.example.companyReputationManagement.dto.review.find.ReviewResponseListDto;
import com.example.companyReputationManagement.dto.review.generate_chart.GenerateChartRequestDto;
import com.example.companyReputationManagement.dto.review.generate_chart.GenerateChartResponse;
import com.example.companyReputationManagement.dto.review.generate_chart.GenerateChartResponseDto;
import com.example.companyReputationManagement.dto.review.get_all.GetReviewRequestDto;
import com.example.companyReputationManagement.dto.review.get_all.GetReviewResponse;
import com.example.companyReputationManagement.dto.review.get_all.GetReviewResponseListDto;
import com.example.companyReputationManagement.dto.review.get_all_by_sent.GetAllBySentRequestDTO;
import com.example.companyReputationManagement.dto.review.get_all_by_sent.GetAllBySentResponse;
import com.example.companyReputationManagement.dto.review.get_all_by_sent.GetAllBySentResponseDTO;
import com.example.companyReputationManagement.dto.review.get_all_by_sent.GetAllBySentResponseListDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.IReviewService;
import com.example.companyReputationManagement.iservice.services.transactions.ReviewsTrans;
import com.example.companyReputationManagement.mapper.ReviewMapper;
import com.example.companyReputationManagement.models.Company;
import com.example.companyReputationManagement.models.CompanySourceUrl;
import com.example.companyReputationManagement.models.Review;
import com.example.companyReputationManagement.models.enums.RoleEnum;
import com.example.companyReputationManagement.models.enums.SentimentTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.example.companyReputationManagement.constants.SysConst.OC_BUGS;
import static com.example.companyReputationManagement.constants.SysConst.OC_OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    private final ReviewMapper reviewMapper;
    private final CompanyDao companyDao;
    private final ReviewsTrans reviewsTrans;
    private final ReviewDao reviewDao;
    private final UserCompanyRolesDao userCompanyRolesDao;
    private final CompanySourceUrlDao companySourceUrlDao;

    private Timestamp dateFormat(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
        return Timestamp.valueOf(localDateTime);
    }

    private void findReviewsOtzovik(Company company, List<Review> reviews, HttpResponseBody<ReviewResponseListDto> response) {
        String url = findUrl(company.getCoreEntityId());
        if (url == null) {
            response.setError("company not found on site");
            response.setMessage("Company not found on site");
        } else {
            getReviewsList(url, company.getCoreEntityId(), reviews);
        }

    }

    private void parseChrome(String url, Long companyId) {
        boolean hasMore = true;
        String urlCom;
        int rating = 0;
        List<Review> reviews = new ArrayList<>();
        int page = 0;
        int seconds = 10;
        Duration duration = Duration.ofSeconds(seconds);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--blink-settings=imagesEnabled=false");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920x1080");
        ChromeDriver driver = new ChromeDriver(options);
        try {
            while (hasMore) {
                try {
                    ++page;
                    urlCom = url + "/comment-page-" + page + "/#comments";
                    driver.get(urlCom);
                    WebDriverWait wait = new WebDriverWait(driver, duration);
                    try {
                        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".wpd-comment")));
                    } catch (TimeoutException e) {
                        System.out.println("Элемент 'comment' не найден. Завершаем обработку.");
                        hasMore = false;
                    }
                    String pageSource = driver.getPageSource();
                    Document doc = Jsoup.parse(pageSource);
                    Elements elements = doc.select(".wpd-comment:not(.wpd-reply)");
                    elementsProc(companyId, reviews, rating, elements);
                    System.out.println(page);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Ошибка запуска драйвера: {}", e.getMessage());
        }
        driver.quit();
        try {
            reviewsTrans.saveAll(reviews);
        } catch (Exception e) {
            log.error("Error saving reviews", e);
            Thread.currentThread().interrupt();
        }
    }

    private void getReviewsList(String url, Long companyId, List<Review> reviews) {
        try {
            int rating = 0;
            Connection.Response response1 = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .referrer("https://www.google.com/")
                    .timeout(15_000)
                    .method(Connection.Method.GET)
                    .execute();
            Document doc = response1.parse();

            Elements elements = doc.select(".wpd-comment:not(.wpd-reply)");
            if (elements.size() >= 99 || elements.isEmpty()) {
                CompletableFuture.runAsync(() -> {
                    parseChrome(url, companyId);
                });

            } else {
                elementsProc(companyId, reviews, rating, elements);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void elementsProc(Long companyId, List<Review> reviews, int rating, Elements elements) {
        for (Element element : elements) {
            Elements reviewBlocks = element.select(".description");
            Elements ratingBlocks = element.select(".wpd-cf-value");
            Elements authors = element.select(".wpd-comment-author");
            Elements dates = element.select(".wpd-comment-date");
            boolean check = !ratingBlocks.isEmpty();
            for (int i = 0; i < reviewBlocks.size(); i++, rating = 0) {
                try {
                    String dateStr = dates.get(i).attr("title");
                    Timestamp date = dateFormat(dateStr);
                    String text = reviewBlocks.get(i).text();
                    if (check && i < ratingBlocks.size()) {
                        Elements stars = ratingBlocks.get(i).select(".fas.fa-star");
                        for (Element star : stars) {
                            if (star.hasClass("wcf-active-star")) {
                                rating++;
                            }
                        }
                    }
                    String author = i < authors.size() ? authors.get(i).text() : "Аноним";
                    Review review = reviewMapper.createReview(text, author, rating, companyId, 1L, date);
                    reviews.add(review);
                } catch (Exception e) {
                    log.error("Error while processing reviews", e);
                    break;
                }
            }
        }
    }

    private Company findCompanyByCode(String companyCode) {
        return companyDao.findByCompanyCode(companyCode);
    }

    private String findUrl(Long companyId) {
        CompanySourceUrl url = companySourceUrlDao.findByCompanyId(companyId);
        return url == null ? null : url.getSourceUrl();
    }

    void parseReviews(Company company) {
        List<Review> reviews = new ArrayList<>();
        String url = findUrl(company.getCoreEntityId());
        if (url == null) {
            log.error("company not found on site");
        } else {
            try {
                getReviewsList(url, company.getCoreEntityId(), reviews);
            } catch (Exception e) {
                log.error("Error while processing reviews");

            }
            try {
                reviewsTrans.saveAll(reviews);
            } catch (Exception e) {

                log.error("Error while saving reviews");
            }
        }
    }

    private boolean hasPermission(Long compId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userCode = jwt.getClaim("userCode");
        RoleEnum currentRole = userCompanyRolesDao.findRoleByUserCode(userCode, compId);
        return currentRole.equals(RoleEnum.OWNER) || currentRole.equals(RoleEnum.ADMIN);
    }

    private boolean checkEmployment(Long compId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userCode = jwt.getClaim("userCode");
        return !userCompanyRolesDao.userExistInCompanyByUserCoe(userCode, compId);
    }

    private byte[] generateAverageChart(List<Review> reviews, String compName) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, List<Integer>> reviewsByDate = new HashMap<>();
        for (Review review : reviews) {
            if (review.getRating() == 0) continue;
            String formattedDate = review.getPublishedDate().toLocalDateTime().toLocalDate().toString().substring(0, 7); // Группировка по году-месяцу
            reviewsByDate.computeIfAbsent(formattedDate, k -> new ArrayList<>()).add(review.getRating());
        }
        for (Map.Entry<String, List<Integer>> entry : reviewsByDate.entrySet()) {
            double averageRating = entry.getValue().stream().mapToDouble(Integer::doubleValue).average().orElse(0);
            dataset.addValue(averageRating, "Average Rating", entry.getKey());
        }
        JFreeChart chart = ChartFactory.createLineChart(
                compName + " Average Rating", // Заголовок
                "Date",         // Ось X
                "Average Rating",          // Ось Y
                dataset,           // Набор данных
                PlotOrientation.VERTICAL,
                true,              // Легенда
                true,              // Подсказки
                false              // Статистика
        );
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4));
        xAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        ChartRenderingInfo info = new ChartRenderingInfo();
        BufferedImage image = chart.createBufferedImage(900, 750, info);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private byte[] generateSentChart(List<Review> reviews, String compName) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<SentimentTypeEnum, Integer> countByType = new EnumMap<>(SentimentTypeEnum.class);
        for (SentimentTypeEnum type : SentimentTypeEnum.values()) {
            countByType.put(type, 0);
        }
        for (Review review : reviews) {
            SentimentTypeEnum type = review.getSentimentTypeId();
            countByType.put(type, countByType.get(type) + 1);
        }
        for (Map.Entry<SentimentTypeEnum, Integer> entry : countByType.entrySet()) {
            String label = switch (entry.getKey()) {
                case POSITIVE -> "Положительный";
                case NEUTRAL -> "Нейтральный";
                case NEGATIVE -> "Отрицательный";
                case UNRATED -> "Неоценённый";
            };
            dataset.addValue(entry.getValue(), "Количество отзывов", label);
        }
        JFreeChart chart = ChartFactory.createBarChart(
                compName + " – Количество отзывов по типу", // Заголовок
                "Тип отзыва",                               // Ось X
                "Количество",                               // Ось Y
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4));
        xAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        ChartRenderingInfo info = new ChartRenderingInfo();
        BufferedImage image = chart.createBufferedImage(900, 750, info);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", byteArrayOutputStream);
        ImageIO.write(image, "PNG", new File("test_chart.png"));
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public HttpResponseBody<ReviewResponseListDto> findReviews(ReviewRequestDto reviewRequestDto) throws Exception {
        HttpResponseBody<ReviewResponseListDto> response = new ReviewResponse();
        Company comp = findCompanyByCode(reviewRequestDto.getCompanyCode());
        if (comp == null) {
            response.setMessage("Company not found");
            response.setError(OC_BUGS);
        } else {
            if (!hasPermission(comp.getCoreEntityId())) {
                response.setMessage("User hasn't permission");
            } else {

                List<Review> reviews = new ArrayList<>();
                findReviewsOtzovik(comp, reviews, response);
                if (!reviews.isEmpty()) {
                    try {
                        reviewsTrans.saveAll(reviews);
                    } catch (Exception e) {
                        response.setError("Error while saving reviews");

                        Thread.currentThread().interrupt();
                    }
                    reviews = reviewDao.findAllByCompanyId(comp.getCoreEntityId());
                    ReviewResponseListDto reviewResponseListDto = new ReviewResponseListDto(
                            reviews.stream().map(reviewMapper::mapReviewToReviewResponseDto).toList());
                    response.setResponseEntity(reviewResponseListDto);
                } else {
                    response.setError("Responses has 100+ messages .Checking response in process...");
                }
            }
        }
        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);
        return response;
    }

    @Override
    public HttpResponseBody<GetReviewResponseListDto> getReviews(GetReviewRequestDto getReviewRequestDto) {
        HttpResponseBody<GetReviewResponseListDto> response = new GetReviewResponse();
        Company comp = findCompanyByCode(getReviewRequestDto.getCompanyCode());
        if (comp == null) {
            response.setMessage("Company not found");
            response.setResponseCode(OC_BUGS);
        } else {
            if (checkEmployment(comp.getCoreEntityId())) {
                response.setMessage("User not in company");
            } else {
                List<Review> responseListDto = reviewDao.findAllByCompanyId(comp.getCoreEntityId());
                GetReviewResponseListDto getReviewResponseListDto = new GetReviewResponseListDto(
                        responseListDto.stream().map(reviewMapper::mapReviewToGetReviewResponseDto).toList());
                if (getReviewResponseListDto.getReviewList().isEmpty()) {
                    response.setMessage("No reviews found");
                } else {
                    response.setResponseEntity(getReviewResponseListDto);

                }
            }
        }
        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);
        return response;
    }

    @Override
    public HttpResponseBody<GenerateChartResponseDto> generateCharts(GenerateChartRequestDto generateChartRequestDto) throws IOException {
        HttpResponseBody<GenerateChartResponseDto> response = new GenerateChartResponse();
        Company comp = findCompanyByCode(generateChartRequestDto.getCompanyCode());
        if (comp == null) {
            response.setMessage("Company not found");
        } else {
            if (checkEmployment(comp.getCoreEntityId())) {
                response.setMessage("User not in company");
            } else {
                List<Review> reviews = reviewDao.findAllByCompanyIdSorted(comp.getCoreEntityId());
                if (reviews.isEmpty()) {
                    response.setMessage("No reviews found");
                } else {
                    byte[] chartAvgImage = generateAverageChart(reviews, comp.getName());
                    byte[] chartBar = generateSentChart(reviews, comp.getName());
                    response.setResponseEntity(new GenerateChartResponseDto(chartAvgImage, chartBar));
                    response.setMessage("Chart generated successfully");

                    // Сохранение изображения в файл
                    //  ImageIO.write(image, "PNG", new File("test_chart.png"));
                }
            }
        }

        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);
        return response;
    }

    @Override
    public HttpResponseBody<GetAllBySentResponseListDTO> getAllReviewsBySentType(GetAllBySentRequestDTO allBySentRequestDTO) {
        HttpResponseBody<GetAllBySentResponseListDTO> response = new GetAllBySentResponse();
        Company comp = findCompanyByCode(allBySentRequestDTO.companyCode());
        if (comp == null) {
            response.setMessage("Company not found");
            response.setResponseCode(OC_BUGS);
        } else {
            if (checkEmployment(comp.getCoreEntityId())) {
                response.setMessage("User not in company");
            } else {
                SentimentTypeEnum type = SentimentTypeEnum.fromId(Math.toIntExact(allBySentRequestDTO.sentId()));
                List<GetAllBySentResponseDTO> reviewList = reviewDao.findAllBySentType(comp.getCoreEntityId(), type);
                if (reviewList.isEmpty()) {
                    response.setMessage("No reviews found");
                } else {
                    GetAllBySentResponseListDTO reviewResponseList = reviewMapper.mapListToGetAllBySentResponseListDTO(comp.getName(), reviewList, type.getType());
                    response.setResponseEntity(reviewResponseList);
                    response.setMessage("All reviews found");
                }
            }
        }
        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);
        return response;
    }


}
