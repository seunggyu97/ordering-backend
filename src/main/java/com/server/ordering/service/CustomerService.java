package com.server.ordering.service;

import com.server.ordering.domain.*;
import com.server.ordering.domain.dto.request.PasswordChangeDto;
import com.server.ordering.domain.member.Customer;
import com.server.ordering.repository.CustomerRepository;
import com.server.ordering.repository.OrderRepository;
import com.server.ordering.repository.RestaurantRepository;
import com.server.ordering.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CustomerService implements MemberService<Customer> {

    private final CustomerRepository customerRepository;
    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;

    /**
     * 고객 회원가입
     * @return 가입한 고객 ID 반환
     */
    @Transactional
    @Override
    public Optional<Long> signUp(Customer customer) {
        customerRepository.save(customer);
        return Optional.of(customer.getId());
    }

    /**
     * 고객 로그인
     * @return 로그인 성공 시 ID를, 실패 시 NULL을 Optional로 반환
     */
    @Override
    public Optional<Customer> signIn(String email, String password) {
        try {
            Customer customer = customerRepository.findByIdAndPassword(email, password);
            return Optional.of(customer);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * 고객 회원가입할 때 email 중복 검증
     */
    @Override
    public boolean isIdDuplicated(String signInId) {
        try {
            customerRepository.findById(signInId);
            return true;
        } catch (NoResultException e) {
            return false;
        }

    }

    /**
     * 고객 휴대폰번호 변경
     */
    @Transactional
    public void putPhoneNumber(Long customerId, String phoneNumber) {
        Customer customer = customerRepository.findOneWithPhoneNumber(customerId);
        PhoneNumber number = new PhoneNumber(phoneNumber, MemberType.CUSTOMER);
        customer.putPhoneNumber(number);
    }

    /**
     * 고객 비밀번호 변경
     */
    @Transactional
    public Boolean putPassword(Long customerId, PasswordChangeDto dto) {
        Customer customer = customerRepository.findOne(customerId);
        if (Objects.equals(customer.getPassword(), dto.getCurrentPassword())) {
            customer.putPassword(dto.getNewPassword());
            return true;
        }
        return false;
    }

    /**
     * 고객 회원탈퇴
     */
    @Transactional
    @Override
    public void deleteAccount(Long id) {
        customerRepository.remove(id);
    }

    @Transactional
    public Boolean registerReview(Long restaurantId, Long orderId, String reviewText) {
        Order order = orderRepository.findOneWithReview(orderId);
        if (order.isAbleRegisterReview()) {
            Review review = new Review(reviewText);
            Restaurant restaurant = restaurantRepository.findOne(restaurantId);
            review.registerOrderAndRestaurant(order, restaurant);
            reviewRepository.save(review);
            return true;
        }
        return false;
    }

    @Transactional
    public void putReview(Long reviewId, String reviewText) {
        reviewRepository.put(reviewId, reviewText);
    }

    @Transactional
    public void removeReview(Long reviewId) {
        Review review = reviewRepository.findOne(reviewId);
        reviewRepository.remove(review);
    }
}
