package jm.stockx.controller.payment;

import jm.stockx.util.PaymentChargeRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckoutController {

    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;

    @GetMapping("/payment/checkout/" + "{amount}/" + "{currency}")
    public String checkout(Model model, @PathVariable String amount, @PathVariable String currency) {
        model.addAttribute("amount", Integer.parseInt(amount)); // in cents
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", PaymentChargeRequest.Currency.valueOf(currency));
        return "paymentCheckout";
    }
}
