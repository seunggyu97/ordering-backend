package com.server.ordering.api;

import com.server.ordering.domain.Food;
import com.server.ordering.domain.RepresentativeMenu;
import com.server.ordering.domain.Restaurant;
import com.server.ordering.domain.dto.request.FoodStatusDto;
import com.server.ordering.domain.dto.request.RestaurantInfoDto;
import com.server.ordering.domain.dto.ResultDto;
import com.server.ordering.domain.dto.FoodDto;
import com.server.ordering.domain.dto.request.SalesRequestDto;
import com.server.ordering.domain.dto.response.DailySalesDto;
import com.server.ordering.domain.dto.response.RestaurantPreviewDto;
import com.server.ordering.service.FoodService;
import com.server.ordering.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class RestaurantApiController {

    private final RestaurantService restaurantService;
    private final FoodService foodService;

    /**
     * 매장 정보 변경
     */
    @PutMapping("/api/restaurant/{restaurantId}")
    public ResultDto<Boolean> putRestaurant(
            @PathVariable Long restaurantId,
            @RequestBody RestaurantInfoDto dto) {
        restaurantService.putRestaurant(restaurantId, dto);
        return new ResultDto<>(1, true);
    }

    /**
     * 매장 음식 추가
     */
    @PostMapping("/api/restaurant/{restaurantId}/food")
    public ResultDto<Optional<Long>> registerFood(
            @PathVariable Long restaurantId,
            @RequestPart(required = false) MultipartFile image,
            @RequestPart FoodDto dto) {

        Optional<Long> foodId = foodService.registerFood(restaurantId, dto, image);
        return new ResultDto<>(1, foodId);
    }

    /**
     * 매장 음식 삭제
     */
    @DeleteMapping("/api/restaurant/food/{foodId}")
    public ResultDto<Boolean> removeFood(@PathVariable Long foodId) {
        Food food = foodService.findFood(foodId);

        String imageUrl = food.getImageUrl();
        if (imageUrl != null) {
            String imageKey = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            // 기존 이미지 삭제
            //s3Service.delete(imageKey);
        }

        foodService.deleteFood(food);
        return new ResultDto<>(1, true);
    }

    /**
     * 매장 음식 정보 변경
     */
    @PutMapping("/api/restaurant/{restaurantId}/food/{foodId}")
    public ResultDto<Boolean> putFood(
            @PathVariable Long restaurantId,
            @PathVariable Long foodId,
            @RequestPart(required = false) MultipartFile image,
            @RequestPart FoodDto dto) {

        foodService.putFood(foodId, restaurantId, dto, image);
        return new ResultDto<>(1, true);
    }

    /**
     * 매장 음식 품절 정보 변경
     */
    @PutMapping("/api/restaurant/food/{foodId}/status")
    public ResultDto<Boolean> putFoodStatus(@PathVariable Long foodId, @RequestBody FoodStatusDto dto) {
        foodService.changeSoldOutStatus(foodId, dto);
        return new ResultDto<>(1, true);
    }

    /**
     * 매장 모든 메뉴 반환
     */
    @PostMapping("/api/restaurant/{restaurantId}/foods")
    public ResultDto<List<FoodDto>> getAllFood(@PathVariable Long restaurantId) {
        List<Food> foods = foodService.getAllFood(restaurantId);
        List<FoodDto> foodDtoList = foods.stream().map(FoodDto::new).collect(Collectors.toList());
        return new ResultDto<>(foods.size(), foodDtoList);
    }

    /**
     * 매장 한 달 매출 반환
     */
    @PostMapping("/api/restaurant/{restaurantId}/sales")
    public ResultDto<List<DailySalesDto>> getDailySales(@PathVariable Long restaurantId, @RequestBody SalesRequestDto dto) {
        List<DailySalesDto> sales = restaurantService.getMonthlySalesOfRestaurant(restaurantId, dto.getFrom(), dto.getBefore());
        return new ResultDto<>(sales.size(), sales);
    }

    /**
     * 매장 프로필 이미지 등록, 변경
     */
    @PutMapping("/api/restaurant/{restaurantId}/profile_image")
    public ResultDto<Boolean> putProfileImage(@PathVariable Long restaurantId, @RequestPart MultipartFile image) {
        restaurantService.putRestaurantProfileImage(restaurantId, image);
        return new ResultDto<>(1, true);
    }

    /**
     * 매장 배경 이미지 등록, 변경
     */
    @PutMapping("/api/restaurant/{restaurantId}/background_image")
    public ResultDto<Boolean> putBackgroundImage(@PathVariable Long restaurantId, @RequestPart MultipartFile image) {
        restaurantService.putRestaurantBackgroundImage(restaurantId, image);
        return new ResultDto<>(1, true);
    }

    /**
     * 매장 대표 음식 추가
     */
    @PostMapping("/api/restaurant/{restaurantId}/representative")
    public ResultDto<Boolean> addRepresentativeMenu(@PathVariable Long restaurantId, @RequestParam(name = "food_id") Long foodId) {
        Boolean result = restaurantService.addRepresentativeMenu(restaurantId, foodId);
        return new ResultDto<>(1, result);
    }

    /**
     * 매장 대표 음식 삭제
     */
    @DeleteMapping("/api/restaurant/{restaurantId}/representative")
    public ResultDto<Boolean> removeRepresentativeMenu(@PathVariable Long restaurantId, @RequestParam(name = "food_id") Long foodId) {
        restaurantService.removeRepresentativeMenu(restaurantId, foodId);
        return new ResultDto<>(1, true);
    }

    /**
     * 매장 목록 반환
     * // 추후 API연동 완료되면 변경 예정
     */
    @PostMapping("/api/restaurants")
    public ResultDto<List<RestaurantPreviewDto>> getRestaurantList() {
        List<Restaurant> restaurants = restaurantService.getAllForPreview();
        List<RestaurantPreviewDto> previewDtos = restaurants.stream().map(restaurant -> {
            List<RepresentativeMenu> representativeMenus = new ArrayList<>(restaurant.getRepresentativeMenus());
            List<String> foodNames = representativeMenus.stream().map(RepresentativeMenu::getFoodName).collect(Collectors.toList());
            return new RestaurantPreviewDto(restaurant.getId(), restaurant.getRestaurantName(), restaurant.getProfileImageUrl(), foodNames);
        }).collect(Collectors.toList());
        return new ResultDto<>(previewDtos.size(), previewDtos);
    }
}
