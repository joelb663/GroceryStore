package grocery.repository;

import grocery.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("select x from CartItem x where x.cartId = :id")
    List<CartItem> findAllCartItemsById(@Param("id") long cart_id);

    @Modifying
    @Query("delete from CartItem x where x.cartId = :cart_id and x.productId = :product_id")
    void deleteById(@Param("cart_id") long cart_id, @Param("product_id") long product_id);
}