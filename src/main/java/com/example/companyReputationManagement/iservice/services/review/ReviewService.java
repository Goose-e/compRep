package com.example.companyReputationManagement.iservice.services.review;

import com.example.companyReputationManagement.dao.CompanyDao;
import com.example.companyReputationManagement.dao.ReviewDao;
import com.example.companyReputationManagement.dto.review.find.ReviewRequestDto;
import com.example.companyReputationManagement.dto.review.find.ReviewResponse;
import com.example.companyReputationManagement.dto.review.find.ReviewResponseListDto;
import com.example.companyReputationManagement.dto.review.get_all.GetReviewRequestDto;
import com.example.companyReputationManagement.dto.review.get_all.GetReviewResponse;
import com.example.companyReputationManagement.dto.review.get_all.GetReviewResponseListDto;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.IReviewService;
import com.example.companyReputationManagement.mapper.ReviewMapper;
import com.example.companyReputationManagement.models.Company;
import com.example.companyReputationManagement.models.Review;
import com.example.companyReputationManagement.models.enums.SourcesEnum;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.example.companyReputationManagement.constants.SysConst.OC_BUGS;
import static com.example.companyReputationManagement.constants.SysConst.OC_OK;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    private final ReviewMapper reviewMapper;
    private final CompanyDao companyDao;
    private final ReviewDao reviewDao;

    private WebDriver createWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920x1080");
        return new ChromeDriver(options);
    }

    private String findCompanyUrl(String companyName, SourcesEnum source) throws Exception {
        WebDriver driver = createWebDriver();
        driver.get(source.getUrl());

        WebElement input = driver.findElement(By.cssSelector("input.is-search-input"));
        input.sendKeys(companyName);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement firstSuggestion = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".is-title a")));

        return firstSuggestion.getAttribute("href");
    }

    private Timestamp dateFormat(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
        return Timestamp.valueOf(localDateTime);
    }

    private void findReviewsOtzovik(Company company, List<Review> reviews, HttpResponseBody<ReviewResponseListDto> response) throws Exception {
        int rating = 0;
        String url = findCompanyUrl(company.getName(), SourcesEnum.OTZOVIK);
        if (url == null) {
            response.setError("company not found on site");
            response.setMessage("Company not found on site");
        } else {
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
                    System.out.println(dates.get(i).attr("title"));
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
                    Review review = reviewMapper.createReview(text, author, rating, company.getCoreEntityId(), 1L, date);

                    reviews.add(review);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    response.setError(e.getMessage());
                    response.setMessage("Error while processing reviews");
                    break;
                }
            }

        }
    }


    @Override
    public HttpResponseBody<ReviewResponseListDto> findReviews(ReviewRequestDto reviewRequestDto) throws Exception {
        HttpResponseBody<ReviewResponseListDto> response = new ReviewResponse();
        Company comp = companyDao.findByCompanyCode(reviewRequestDto.getCompanyCode());
        if (comp == null) {
            response.setMessage("Company not found");
            response.setResponseCode(OC_BUGS);
        } else {
            List<Review> reviews = new ArrayList<>();
            findReviewsOtzovik(comp, reviews, response);
            if (!reviews.isEmpty()) {
                reviews.forEach(
                        review -> {
                            if (!reviewDao.existsByIdAndText(review.getCompanyId(), review.getContent())) {
                                reviewDao.save(review);
                            }
                        }
                );
                List<Review> uniqueReviews = reviewDao.findAllByCompanyId(comp.getCoreEntityId());
                ReviewResponseListDto reviewResponseListDto = new ReviewResponseListDto(
                        uniqueReviews.stream().map(reviewMapper::mapReviewToReviewResponseDto).toList());
                response.setResponseEntity(reviewResponseListDto);
            } else {
                response.setMessage("No reviews found");
            }
        }
        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);
        return response;
    }

    @Override
    public HttpResponseBody<GetReviewResponseListDto> getReviews(GetReviewRequestDto getReviewRequestDto) {
        HttpResponseBody<GetReviewResponseListDto> response = new GetReviewResponse();
        Company comp = companyDao.findByCompanyCode(getReviewRequestDto.getCompanyCode());
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

}
