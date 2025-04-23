package com.example.companyReputationManagement.iservice.services.review;

import com.example.companyReputationManagement.dao.CompanyDao;
import com.example.companyReputationManagement.dao.ReviewDao;
import com.example.companyReputationManagement.dao.SourceDao;
import com.example.companyReputationManagement.dto.review.find.ReviewRequestDto;
import com.example.companyReputationManagement.dto.review.find.ReviewResponse;
import com.example.companyReputationManagement.dto.review.find.ReviewResponseListDto;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.IReviewScraperService;
import com.example.companyReputationManagement.mapper.ReviewMapper;
import com.example.companyReputationManagement.mapper.SourceMapper;
import com.example.companyReputationManagement.models.Company;
import com.example.companyReputationManagement.models.Review;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.companyReputationManagement.constants.SysConst.OC_BUGS;
import static com.example.companyReputationManagement.constants.SysConst.OC_OK;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewScraperService {
    private final ReviewMapper reviewMapper;
    private final CompanyDao companyDao;
    private final ReviewDao reviewDao;
    private final SourceMapper sourceMapper;
    private final SourceDao sourceDao;

    @Override
    public HttpResponseBody<ReviewResponseListDto> getReviews(ReviewRequestDto reviewRequestDto) throws IOException, InterruptedException {
        HttpResponseBody<ReviewResponseListDto> response = new ReviewResponse();
        Company comp = companyDao.findByCompanyCode(reviewRequestDto.getCompanyCode());

        if (comp == null) {
            response.setMessage("Company not found");
            response.setResponseCode(OC_BUGS);
        } else {

            // Получаем HTML-страницу с отзывами
            Connection.Response response1 = Jsoup.connect(reviewRequestDto.getUrl())
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .method(Connection.Method.GET)
                    .execute();

            Document doc = response1.parse();

            Elements reviewBlocks = doc.select(".description");
            Elements ratingBlocks = doc.select(".wpd-cf-value");
            Elements authors = doc.select(".wpd-comment-author");

            List<Review> reviews = new ArrayList<>();
            for (int i = 0; i < reviewBlocks.size(); i++) {
                try {
                    String text = reviewBlocks.get(i).text();
                    Elements stars = ratingBlocks.get(i).select(".fas.fa-star");
                    int rating = 0;
                    for (Element star : stars) {

                        if (star.hasClass("wcf-active-star")) {
                            rating++;
                        }
                    }


                    String author = i < authors.size() ? authors.get(i).text() : "Аноним";
                    Review review = reviewMapper.createReview(text, author, rating, comp.getCoreEntityId(), 1L);
                    reviews.add(review);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    response.setError(e.getMessage());
                    response.setMessage("Error while processing reviews");
                    break;
                }
            }


            if (!reviews.isEmpty()) {
                reviewDao.saveAll(reviews);
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
}
