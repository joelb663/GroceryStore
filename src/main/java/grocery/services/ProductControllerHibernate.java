package grocery.services;

import grocery.model.Cart;
import grocery.model.CartItem;
import grocery.model.Product;
import grocery.repository.CartRepository;
import grocery.repository.ProductRepository;
import grocery.repository.CartItemRepository;
import grocery.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProductControllerHibernate {
    private static final Logger LOGGER = LogManager.getLogger();

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    private List<Product> fetchedProducts;
    private Long cartId;
    private int itemCount = 0;
    private String currentFilter = "";
    private String fetchingParams = "";
    private String headerText = "";

    public ProductControllerHibernate(ProductRepository productRepository, UserRepository userRepository, CartItemRepository cartItemRepository, CartRepository cartRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
    }

    //make a get mapping that send out blank form registering
    //results from that form go to a post mapping that adds that person then goes into the main page

    //adding stuff to cart and quanities can be get mappings
    //only sensitive information should use post mapping

    @GetMapping("/filter")
    public String FilterProducts(Model model, @RequestParam String filterType){
        if (!currentFilter.equals(filterType)) {
            filterProducts(filterType, fetchedProducts.size());
            currentFilter = filterType;
        }

        addAttributes(model);
        return "productPage";
    }

    @GetMapping("/cart")
    public String addToCart(Model model, @RequestParam Long id, @RequestParam Long quantity, @RequestParam String page){
        CartItem item = new CartItem();
        item.setCartId(cartId);
        item.setProductId(id);
        item.setQuantity(quantity);

        item = cartItemRepository.save(item);
        itemCount += item.getQuantity();
        LOGGER.info("Added product with id: " + item.getProductId() + " to cart with id: " + cartId);

        if (page.equals("main"))
            addAttributes2(model);
        else
            addAttributes(model);

        return page;
    }

    @GetMapping("/main")
    public String MainPage(Model model, @RequestHeader Map<String, String> headers, @RequestParam(required = false) Long id, @RequestParam(required = false) Long quantity) {
        if(cartId == null){
            Cart newCart = new Cart();
            newCart.setUserId(1L);
            newCart = cartRepository.save(newCart);
            cartId = newCart.getId();
        }

        addAttributes2(model);
        return "main";
    }

    @GetMapping("/category")
    public String fetchProductByCategory(Model model, @RequestParam String category){
        if (!fetchingParams.equals(category)){
            fetchingParams = category;
            fetchedProducts = productRepository.findProductByCategoryOrderByNameAsc(category);
            currentFilter = "A-Z";
            headerText = category;
        }

        addAttributes(model);
        return "productPage";
    }

    @GetMapping("/search")
    public String fetchProductBySearch(Model model, @RequestParam String search){
        if (!fetchingParams.equals(search)){
            fetchingParams = search;
            fetchedProducts = productRepository.findProductByNameOrderByNameAsc(search);
            currentFilter = "A-Z";
            headerText = "Showing results for \"" + search + "\"";
        }

        addAttributes(model);
        return "productPage";
    }

    @GetMapping("/myCart")
    @Transactional(rollbackFor = { SQLException.class })
    public String shoppingCart(Model model, @RequestParam(required = false) Long id){
        double priceTotal = 0;
        int itemCounter = 0;

        if (id != null)
            cartItemRepository.deleteById(cartId, id);

        List<CartItem> cart = cartItemRepository.findAllCartItemsById(cartId);
        HashMap<Product, Long> cartProducts = new HashMap<>();

        for (CartItem i : cart) {
            Product x = productRepository.findById(i.getProductId()).get();
            cartProducts.put(x, i.getQuantity());
            priceTotal += x.getPrice() * i.getQuantity();
            itemCounter += i.getQuantity();
        }

        model.addAttribute("cartItems", cartProducts);
        model.addAttribute("itemCount", itemCounter);
        model.addAttribute("total", priceTotal);

        return "cartPage";
    }

    public void filterProducts(String filter, int itemCount){
        switch(filter) {
            case "A-Z":
                if (itemCount < 10)
                    fetchedProducts = productRepository.findProductByNameOrderByNameAsc(fetchingParams);
                else
                    fetchedProducts = productRepository.findProductByCategoryOrderByNameAsc(fetchingParams);
                break;
            case "Z-A":
                if (itemCount < 10)
                    fetchedProducts = productRepository.findProductByNameOrderByNameDesc(fetchingParams);
                else
                    fetchedProducts = productRepository.findProductByCategoryOrderByNameDesc(fetchingParams);
                break;
            case "HighToLow":
                if (itemCount < 10)
                    fetchedProducts = productRepository.findProductByNameOrderByPriceDesc(fetchingParams);
                else
                    fetchedProducts = productRepository.findProductByCategoryOrderByPriceDesc(fetchingParams);
                break;
            case "LowToHigh":
                if (itemCount < 10)
                    fetchedProducts = productRepository.findProductByNameOrderByPriceAsc(fetchingParams);
                else
                    fetchedProducts = productRepository.findProductByCategoryOrderByPriceAsc(fetchingParams);
                break;
            case "Stock":
                if (itemCount < 10)
                    fetchedProducts = productRepository.findProductByNameOrderByStock(fetchingParams);
                else
                    fetchedProducts = productRepository.findProductByCategoryOrderByStock(fetchingParams);
                break;
        }
    }

    public void addAttributes(Model model){
        model.addAttribute("itemCount", itemCount);
        model.addAttribute("Products", fetchedProducts);
        model.addAttribute("fetchedAmount", fetchedProducts.size() + " results Filtered " + currentFilter);
        model.addAttribute("header", headerText);
    }

    public void addAttributes2(Model model){
        model.addAttribute("Produce", productRepository.findProductByCategory("Produce"));
        model.addAttribute("Meat", productRepository.findProductByCategory("Meat"));
        model.addAttribute("Dairy", productRepository.findProductByCategory("Dairy"));
        model.addAttribute("Bakery", productRepository.findProductByCategory("Bakery"));
        model.addAttribute("itemCount", itemCount);

    }
}