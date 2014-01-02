package com.devteam.accounting.web.controller;


import com.devteam.accounting.service.AccountService;
import com.devteam.accounting.service.dto.AccountDto;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<AccountDto>> getAccount() {
        List<AccountDto> accounts = accountService.findAlls();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity save(@RequestBody final AccountDto dto) {
        accountService.save(dto);
        return new ResponseEntity(HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/get/{id}")
    public ResponseEntity<AccountDto> getAccount(@PathVariable("id") Long id) {
        AccountDto account = accountService.findById(id);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }


}
