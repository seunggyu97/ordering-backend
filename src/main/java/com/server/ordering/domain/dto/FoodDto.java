package com.server.ordering.domain.dto;

import com.server.ordering.domain.Food;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import static lombok.AccessLevel.PROTECTED;
import static lombok.AccessLevel.PUBLIC;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = PUBLIC)
public class FoodDto {

    // request 전용
    private String foodName;
    private int price;
    private String menuIntro;

    private Long foodId;
    private String imageUrl;
    private boolean soldOut;

    public FoodDto(Food food) {
        this.foodId = food.getId();
        this.foodName = food.getFoodName();
        this.price = food.getPrice();
        this.soldOut = food.isSoldOut();
        this.imageUrl = food.getImageUrl();
        this.menuIntro = food.getMenuIntro();
    }
}
