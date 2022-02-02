package grocery.repository;

import grocery.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select x from Product x where x.category = :category")
    List<Product> findProductByCategory(@Param("category") String category);

    @Query("select x from Product x where x.category = :category order by x.name")
    List<Product> findProductByCategoryOrderByNameAsc(@Param("category") String category);

    @Query("select x from Product x where x.category = :category order by x.name DESC")
    List<Product> findProductByCategoryOrderByNameDesc(@Param("category") String category);

    @Query("select x from Product x where x.category = :category order by x.price")
    List<Product> findProductByCategoryOrderByPriceAsc(@Param("category") String category);

    @Query("select x from Product x where x.category = :category order by x.price DESC")
    List<Product> findProductByCategoryOrderByPriceDesc(@Param("category") String category);

    @Query("select x from Product x where x.category = :category order by x.stock DESC")
    List<Product> findProductByCategoryOrderByStock(@Param("category") String category);

    @Query("select x from Product x where x.name like :input% order by x.name")
    List<Product> findProductByNameOrderByNameAsc(@Param("input") String input);

    @Query("select x from Product x where x.name like :input% order by x.name DESC")
    List<Product> findProductByNameOrderByNameDesc(@Param("input") String input);

    @Query("select x from Product x where x.name like :input% order by x.price")
    List<Product> findProductByNameOrderByPriceAsc(@Param("input") String input);

    @Query("select x from Product x where x.name like :input% order by x.price DESC")
    List<Product> findProductByNameOrderByPriceDesc(@Param("input") String input);

    @Query("select x from Product x where x.name like :input% order by x.stock DESC")
    List<Product> findProductByNameOrderByStock(@Param("input") String input);
}