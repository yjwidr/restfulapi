package com.xxx.autoupdate.apiserver.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xxx.autoupdate.apiserver.model.UserEntity;
import com.xxx.autoupdate.apiserver.model.constant.Constants;
import com.xxx.autoupdate.apiserver.model.parameter.LoginUser;
import com.xxx.autoupdate.apiserver.services.UserService;
import com.xxx.autoupdate.apiserver.token.JwtToken;
import com.xxx.autoupdate.apiserver.util.CommonUtils;
import com.xxx.autoupdate.apiserver.util.ResponseEntity;
@RestController
@RequestMapping(Constants.AUTH)
public class LoginController {

}
