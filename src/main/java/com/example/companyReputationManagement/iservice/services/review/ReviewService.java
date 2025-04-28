package com.example.companyReputationManagement.iservice.services.review;

import com.example.companyReputationManagement.dao.CompanyDao;
import com.example.companyReputationManagement.dao.ReviewDao;
import com.example.companyReputationManagement.dto.review.find.ReviewRequestDto;
import com.example.companyReputationManagement.dto.review.find.ReviewResponse;
import com.example.companyReputationManagement.dto.review.find.ReviewResponseListDto;
import com.example.companyReputationManagement.dto.review.generate_chart.GenerateChartRequestDto;
import com.example.companyReputationManagement.dto.review.generate_chart.GenerateChartResponse;
import com.example.companyReputationManagement.dto.review.generate_chart.GenerateChartResponseDto;
import com.example.companyReputationManagement.dto.review.get_all.GetReviewRequestDto;
import com.example.companyReputationManagement.dto.review.get_all.GetReviewResponse;
import com.example.companyReputationManagement.dto.review.get_all.GetReviewResponseListDto;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.IReviewService;
import com.example.companyReputationManagement.iservice.services.transactions.ReviewsTrans;
import com.example.companyReputationManagement.mapper.ReviewMapper;
import com.example.companyReputationManagement.models.Company;
import com.example.companyReputationManagement.models.Review;
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
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

    private Timestamp dateFormat(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
        return Timestamp.valueOf(localDateTime);
    }

    void findReviewsOtzovik(Company company, List<Review> reviews, HttpResponseBody<ReviewResponseListDto> response) throws Exception {
        String url = company.getOtzovikUrl();
        if (url == null) {
            response.setError("company not found on site");
            response.setMessage("Company not found on site");
        } else {
            getReviewsList(url, company.getCoreEntityId(), reviews);
        }
    }

    private void getReviewsList(String url, Long companyId, List<Review> reviews) throws IOException {
        int rating = 0;
        Connection.Response response1 = Jsoup.connect(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                .method(Connection.Method.GET)
                .execute();
        Document doc = response1.parse();

        Elements reviewBlocks = doc.select(".description");
        Elements ratingBlocks = doc.select(".wpd-cf-value");
        Elements authors = doc.select(".wpd-comment-author");
        Elements dates = doc.select(".wpd-comment-date");
        boolean check = !ratingBlocks.isEmpty();
        for (int i = 0; i < reviewBlocks.size(); i++, rating = 0) {
            try {

                String dateStr = dates.get(i).attr("title");
                Timestamp date = dateFormat(dateStr);

                String text = reviewBlocks.get(i).text();
                if (check) {
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
                log.error("Error while processing reviews");
                break;
            }
        }

    }

    void parseReviews(Company company) {
        List<Review> reviews = new ArrayList<>();
        String url = company.getOtzovikUrl();
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

    @Override
    public HttpResponseBody<ReviewResponseListDto> findReviews(ReviewRequestDto reviewRequestDto) throws Exception {
        HttpResponseBody<ReviewResponseListDto> response = new ReviewResponse();
        Company comp = findCompanyByCode(reviewRequestDto.getCompanyCode());
        if (comp == null) {
            response.setMessage("Company not found");
            response.setResponseCode(OC_BUGS);
        } else {
            List<Review> reviews = new ArrayList<>();
            findReviewsOtzovik(comp, reviews, response);
            if (!reviews.isEmpty()) {
                try {
                    reviewsTrans.saveAll(reviews);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }

                reviews = reviewDao.findAllByCompanyId(comp.getCoreEntityId());
                ReviewResponseListDto reviewResponseListDto = new ReviewResponseListDto(
                        reviews.stream().map(reviewMapper::mapReviewToReviewResponseDto).toList());
                response.setResponseEntity(reviewResponseListDto);
            } else {
                response.setMessage("No reviews found");
            }
        }
        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);
        return response;
    }

    private Company findCompanyByCode(String companyCode) {
        return companyDao.findByCompanyCode(companyCode);
    }


    @Override
    public HttpResponseBody<GetReviewResponseListDto> getReviews(GetReviewRequestDto getReviewRequestDto) {
        HttpResponseBody<GetReviewResponseListDto> response = new GetReviewResponse();
        Company comp = findCompanyByCode(getReviewRequestDto.getCompanyCode());
        if (comp == null) {
            response.setMessage("Company not found");
            response.setResponseCode(OC_BUGS);
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
        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);
        return response;
    }

    @Override
    public HttpResponseBody<GenerateChartResponseDto> generateChart(GenerateChartRequestDto generateChartRequestDto) throws IOException {
        HttpResponseBody<GenerateChartResponseDto> response = new GenerateChartResponse();
        Company comp = findCompanyByCode(generateChartRequestDto.getCompanyCode());
        if (comp == null) {
            response.setMessage("Company not found");
        } else {
            List<Review> reviews = reviewDao.findAllByCompanyIdSorted(comp.getCoreEntityId());
            if (reviews.isEmpty()) {
                response.setMessage("No reviews found");
            } else {
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                for (Review review : reviews) {
                    if (review.getRating() == 0) continue;
                    String formattedDate = review.getPublishedDate()
                            .toLocalDateTime()
                            .toLocalDate()
                            .toString();
                    dataset.addValue(review.getRating(), "Rating", formattedDate);
                }
                JFreeChart chart = ChartFactory.createLineChart(
                        comp.getName() + " Rating", // Заголовок
                        comp.getName(),         // Ось X
                        "Rating",          // Ось Y
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
                byte[] chartImage = byteArrayOutputStream.toByteArray();
                response.setResponseEntity(new GenerateChartResponseDto(chartImage));
                response.setMessage("Chart generated successfully");
//                ImageIO.write(image, "PNG", new File("test_chart.png"));
            }
        }
        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);
        return response;
    }

}
