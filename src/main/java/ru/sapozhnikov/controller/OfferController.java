package ru.sapozhnikov.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.sapozhnikov.dao.CustomerDAO;
import ru.sapozhnikov.dao.OfferDAO;
import ru.sapozhnikov.dao.OrderDAO;
import ru.sapozhnikov.dao.StatusDAO;
import ru.sapozhnikov.entity.Offer;
import ru.sapozhnikov.entity.Order;
import ru.sapozhnikov.entity.Status;
import ru.sapozhnikov.security.GetToken;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/orders")
public class OfferController {
    private OrderDAO orderDAO;
    private StatusDAO statusDAO;
    private OfferDAO offerDAO;
    private CustomerDAO customerDAO;
    private GetToken getToken;
    @Value("${orderNode.url}")
    private StringBuilder customerUrl;

    @Autowired
    public OfferController(OrderDAO orderDAO, StatusDAO statusDAO,
                           OfferDAO offerDAO, CustomerDAO customerDAO, GetToken getToken) {
        this.orderDAO = orderDAO;
        this.statusDAO = statusDAO;
        this.offerDAO = offerDAO;
        this.customerDAO = customerDAO;
        this.getToken = getToken;
    }

    @GetMapping
    public ResponseEntity showAll(){
        return ResponseEntity.ok(orderDAO.findAll());
    }

    @GetMapping(params = "id")
    public ResponseEntity showById(@RequestParam String id){
        try{
            return ResponseEntity.ok(orderDAO.findById(Integer.valueOf(id)).get());
        } catch (Exception e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/offers")
    public ResponseEntity showOffer(String id){
        String token = getToken.getToken();
        if (token == null) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(customerUrl.append("/offers").toString())
                .queryParam("id", id);

        ResponseEntity<String> response = new RestTemplate().exchange(builder.toUriString(),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);

        ObjectMapper objectMapper = new ObjectMapper();

        Offer offer;
        try {
            offer = objectMapper.readValue(response.getBody(), Offer.class);
        } catch (JsonProcessingException e) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(offer);
    }

    @PostMapping
    public ResponseEntity add(String name, boolean paid, String deliveryTime,
                              @RequestParam(name = "status") String statusName,
                              @RequestParam(name = "offer") String offerId,
                              @RequestParam(name = "customer")String customerId){
        Order order = new Order();
        order.setName(name);
        order.setPaid(paid);

        /*10:05 10-05-2020*/
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");
        try{
            order.setDeliveryTime(LocalDateTime.parse(deliveryTime, formatter));
        } catch (Exception e){
            return new ResponseEntity("Incorrect date-time format.",HttpStatus.NOT_ACCEPTABLE);
        }

        try{
            order.setOffer(offerDAO.findById(Integer.parseInt(offerId)).get());
        } catch (Exception e){
            return new ResponseEntity("Incorrect offer.",HttpStatus.NOT_ACCEPTABLE);
        }

        try{
            order.setCustomer(customerDAO.findById(Integer.parseInt(customerId)).get());
        } catch (Exception e){
            return new ResponseEntity("Incorrect customer.",HttpStatus.NOT_ACCEPTABLE);
        }

        Status status = new Status();
        status.setName(statusName);
        status = statusDAO.save(status);
        order.setStatus(status);

        orderDAO.save(order);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity delete(@RequestParam String id){
        try{
            orderDAO.delete(orderDAO.findById(Integer.valueOf(id)).get());
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } catch (NumberFormatException e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PutMapping
    public ResponseEntity update(@RequestParam String id, @RequestParam String field,
                                         @RequestParam String value){
        Order order;
        try{
            order = orderDAO.findById(Integer.valueOf(id)).get();
        } catch (Exception e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }


        switch (field){
            case "name" : order.setName(value); break;
            case "paid" :
                try {
                    order.setPaid(Boolean.parseBoolean(value));
                } catch (Exception e){
                    return new ResponseEntity("Incorrect paid.",HttpStatus.NOT_ACCEPTABLE);
                }
                break;
            case "deliveryTime" :
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");
                try{
                    order.setDeliveryTime(LocalDateTime.parse(value, formatter));
                } catch (Exception e){
                    return new ResponseEntity("Incorrect date-time format.",HttpStatus.NOT_ACCEPTABLE);
                }
                break;
            case "offer" :
                try{
                    order.setOffer(offerDAO.findById(Integer.parseInt(value)).get());
                } catch (Exception e){
                    return new ResponseEntity("Incorrect offer.",HttpStatus.NOT_ACCEPTABLE);
                }
                break;
            case "customer" :
                try{
                    order.setCustomer(customerDAO.findById(Integer.parseInt(value)).get());
                } catch (Exception e){
                    return new ResponseEntity("Incorrect customer.",HttpStatus.NOT_ACCEPTABLE);
                }
                break;
            default:
                return new ResponseEntity("Nothing change.", HttpStatus.NOT_ACCEPTABLE);
        }
        orderDAO.save(order);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
}
