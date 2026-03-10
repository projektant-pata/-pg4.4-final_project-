package cz.projektant_pata.pg4.__FinalProject.controller;

import cz.projektant_pata.pg4.__FinalProject.dto.CartItemDTO;
import cz.projektant_pata.pg4.__FinalProject.dto.ShoppingCartDTO;
import cz.projektant_pata.pg4.__FinalProject.shared.entity.*;
import cz.projektant_pata.pg4.__FinalProject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryItemService categoryItemService;

    @Autowired
    private ChangeableService changeableService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ReceiptService receiptService;

        @GetMapping
    public String orderPage(Model model, HttpSession session) {
        ShoppingCartDTO cart = getCart(session);

        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("allItems", categoryItemService.findAll());
        model.addAttribute("changeables", changeableService.findAll());
        model.addAttribute("cart", cart);

        return "order/index";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int count,
                            @RequestParam(required = false) List<Long> changeableIds,
                            HttpSession session) {
        ShoppingCartDTO cart = getCart(session);

        CategoryItem product = categoryItemService.findById(productId).orElseThrow();

        CartItemDTO cartItem = new CartItemDTO();
        cartItem.setProduct(product);
        cartItem.setCount(count);

        if (changeableIds != null) {
            List<Changeable> selectedChangeables = new ArrayList<>();
            for (Long id : changeableIds) {
                changeableService.findById(id).ifPresent(selectedChangeables::add);
            }
            cartItem.setSelectedChangeables(selectedChangeables);
        }

        cart.addItem(cartItem);
        return "redirect:/order";
    }

    @PostMapping("/remove/{index}")
    public String removeFromCart(@PathVariable int index, HttpSession session) {
        ShoppingCartDTO cart = getCart(session);
        cart.removeItem(index);
        return "redirect:/order";
    }

    @PostMapping("/cancel")
    public String cancelOrder(HttpSession session) {
        session.removeAttribute("cart");
        return "redirect:/order";
    }

    @PostMapping("/complete")
    public String completeOrder(HttpSession session, Model model) {
        ShoppingCartDTO cart = getCart(session);

        if (cart.getItems().isEmpty()) {
            return "redirect:/order";
        }

        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setTime(LocalDateTime.now());
        order.setStatus("PREPARING");
        order.setPrice(cart.getTotalPrice());

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItemDTO cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setFood(cartItem.getProduct().getName());
            orderItem.setCount(cartItem.getCount());
            orderItem.setPrice(cartItem.getTotalPrice());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        Order savedOrder = orderService.save(order);

        String receiptPath = receiptService.generateReceipt(savedOrder, cart);

        model.addAttribute("order", savedOrder);
        model.addAttribute("receiptPath", receiptPath);

        session.removeAttribute("cart");

        return "order/confirmation";
    }

    private ShoppingCartDTO getCart(HttpSession session) {
        ShoppingCartDTO cart = (ShoppingCartDTO) session.getAttribute("cart");
        if (cart == null) {
            cart = new ShoppingCartDTO();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
    @GetMapping("/status")
    @ResponseBody
    public List<Order> getOrdersByStatus() {
        return orderService.findByStatus("PREPARING");

    }
}
