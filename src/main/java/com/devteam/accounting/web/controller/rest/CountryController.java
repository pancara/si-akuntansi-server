package com.devteam.accounting.web.controller.rest;


import com.devteam.accounting.dto.CountryDto;
import com.devteam.accounting.dto.error.ErrorDto;
import com.devteam.accounting.dto.error.ValidationErrorDto;
import com.devteam.accounting.dto.validator.DtoValidator;
import com.devteam.accounting.service.CountryService;
import com.devteam.accounting.service.wrapper.QueryResult;
import com.devteam.accounting.web.controller.params.BrowseParams;
import com.devteam.accounting.web.controller.params.Order;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.OptimisticLockException;
import java.util.List;

@Controller
@RequestMapping("/country")
public class CountryController {

    @Autowired
    private CountryService countryService;

    @Autowired
    private DtoValidator validator;

    @RequestMapping(value = "/browse", method = RequestMethod.POST)
    public ResponseEntity<QueryResult> getCountries(@RequestBody BrowseParams params) {
        if (StringUtils.isEmpty(params.getKeyword())) {
            QueryResult result = countryService.findAlls(params.getOrders(), params.getStart(), params.getCount());
            return new ResponseEntity(result, HttpStatus.OK);
        } else {
            String querySearch = String.format("%%%s%%", params.getKeyword());
            QueryResult result = countryService.findByKeyword(params.getOrders(), params.getStart(),
                    params.getCount(), querySearch);
            return new ResponseEntity(result, HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity getCountry(@PathVariable("id") Long id) {
        CountryDto dto = countryService.findById(id);
        return (dto != null) ?
                new ResponseEntity(dto, HttpStatus.OK) :
                new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity save(@RequestBody final CountryDto dto) {
        ValidationErrorDto errors = validator.validate(dto);
        if (errors.hasErrors()) {
            return new ResponseEntity(errors, HttpStatus.FORBIDDEN);
        }

        try {
            countryService.save(dto);
            return new ResponseEntity(dto, HttpStatus.OK);
        } catch (OptimisticLockException ole) {
            return responseConflictError(ole);
        } catch (Exception e) {
            return responseSqlError(e);
        }
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody final CountryDto dto) {
        ValidationErrorDto errors = validator.validate(dto);
        if (errors.hasErrors()) {
            return new ResponseEntity(errors, HttpStatus.FORBIDDEN);
        }

        try {
            countryService.update(dto);
            return new ResponseEntity(dto, HttpStatus.OK);
        } catch (OptimisticLockException ole) {
            return responseConflictError(ole);
        } catch (Exception e) {
            return responseSqlError(e);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        try {
            countryService.removeById(id);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private ResponseEntity responseConflictError(Exception e) {
        ValidationErrorDto errors = new ValidationErrorDto();
        errors.addError(new ErrorDto(ErrorDto.MESSAGE_LOCKING_ERROR, e.getMessage()));
        return new ResponseEntity(errors, HttpStatus.CONFLICT);
    }

    private ResponseEntity responseSqlError(Exception e) {
        ValidationErrorDto errors = new ValidationErrorDto();
        errors.addError(new ErrorDto(ErrorDto.MESSAGE_SQL_ERROR, e.getCause().getMessage()));
        return new ResponseEntity(errors, HttpStatus.CONFLICT);
    }


}
