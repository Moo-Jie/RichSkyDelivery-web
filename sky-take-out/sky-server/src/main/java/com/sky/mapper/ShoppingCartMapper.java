package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    List<ShoppingCart> selectList(ShoppingCart shoppingCart);

    void updateById(ShoppingCart shoppingCart);

    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) " +
            "values (#{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})")
    void insert(ShoppingCart shoppingCart);

    @Insert("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(ShoppingCart build);

    void subShoppingCartByDishIdOrsetmealId(ShoppingCartDTO shoppingCartDTO);


    void insertBatch(List<ShoppingCart> shoppingCarts);
}
