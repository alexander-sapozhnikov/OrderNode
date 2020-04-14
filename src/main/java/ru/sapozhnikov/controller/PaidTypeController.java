package ru.sapozhnikov.controller;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sapozhnikov.dao.AddressDAO;
import ru.sapozhnikov.dao.CustomerDAO;
import ru.sapozhnikov.dao.PaidTypeDAO;
import ru.sapozhnikov.entity.Address;
import ru.sapozhnikov.entity.Customer;
import ru.sapozhnikov.entity.PaidType;

@RestController
public class PaidTypeController {
    private CustomerDAO customerDAO;
    private PaidTypeDAO paidTypeDAO;

    @Autowired
    public PaidTypeController(CustomerDAO customerDAO, PaidTypeDAO paidTypeDAO) {
        this.customerDAO = customerDAO;
        this.paidTypeDAO = paidTypeDAO;
    }


    @GetMapping(path = "paidTypes")
    public ResponseEntity showAllCustomers(){
        return ResponseEntity.ok(paidTypeDAO.findAll());
    }

    @GetMapping("paidType")
    public ResponseEntity showByIdPaidType(@RequestParam String id){
        try{
            return ResponseEntity.ok(paidTypeDAO.getById(Integer.valueOf(id)));
        } catch (NumberFormatException e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("paidType")
    public ResponseEntity addPaidType (String name){
        PaidType paidType = new PaidType();
        paidType.setName(name);
        paidTypeDAO.save(paidType);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("paidType")
    public ResponseEntity deletePaidType(@RequestParam String id){
        try{
            paidTypeDAO.delete(paidTypeDAO.getById(Integer.valueOf(id)));
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } catch (NumberFormatException e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("paidType")
    public ResponseEntity updatePaidType(@RequestParam String id, @RequestParam String name){
        PaidType paidType = null;
        try{
            paidType = paidTypeDAO.getById(Integer.valueOf(id));
        } catch (NumberFormatException e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        paidType.setName(name);
        paidTypeDAO.save(paidType);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
}
