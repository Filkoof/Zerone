package ru.example.group.main.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.request.SupportRequestDto;
import ru.example.group.main.dto.response.ResultMessageDto;
import ru.example.group.main.dto.response.SupportRequestsDto;
import ru.example.group.main.exception.EmailNotSentException;
import ru.example.group.main.exception.SupportRequestException;
import ru.example.group.main.service.SupportService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
@Api("Requests to support operations controller")
public class SupportController {

    private final SupportService supportService;

    @PostMapping("/support")
    @ApiOperation("Operation to get and save user request to support")
    public ResponseEntity<ResultMessageDto> getSupportRequestAndSaveToDB(@RequestBody SupportRequestDto supportRequestDto) throws SupportRequestException, EmailNotSentException {
        return supportService.receiveSupportRequest(supportRequestDto);
    }

    @GetMapping("/support")
    @ApiOperation("Operation to get all requests to adminka")
    public List<SupportRequestsDto> getAllSupportRequest(){
        return supportService.getAllSupportRequests();
    }

    @GetMapping("/support/request/{id}")
    @ApiOperation("Operation to get support request by id to adminka")
    public SupportRequestsDto getSupportRequestById(@PathVariable @Min(1) long id){
        return supportService.getSupportRequestById(id);
    }

    @PostMapping("/support/update/{id}")
    @ApiOperation("Operation to change support request status by id to adminka")
    public SupportRequestsDto changeSupportRequestStatusById(@PathVariable @Min(1) long id,
                                                             @RequestParam @NotEmpty String status) throws SupportRequestException {
        return supportService.changeSupportRequestStatusById(id, status);
    }

}
