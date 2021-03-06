package com.server.ordering.repository;

import com.server.ordering.domain.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class ReviewRepository {

    private final EntityManager em;
    private final JdbcTemplate jdbcTemplate;

    public void save(Review review) {
        em.persist(review);
    }

    public Review findOne(Long reviewId) {
        return em.find(Review.class, reviewId);
    }

    public void put(Long reviewId, String reviewText) {
        jdbcTemplate.update("update review set review=? where review_id=?", reviewText, reviewId);
    }

    public void remove(Review review) {
        em.remove(review);
    }
}
