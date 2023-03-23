package com.tayobus.sugamaga.api.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.tayobus.sugamaga.api.common.utils.TokenUtils;
import com.tayobus.sugamaga.api.request.HistoryRequest;
import com.tayobus.sugamaga.api.service.GameService;
import com.tayobus.sugamaga.db.entity.DropTable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
@Api(tags = "게임 API")
public class GameController {
    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    private final static String SUCCESS = "Success";
    private final static String FAIL = "Fail";



    @Autowired
    private GameService gameService;

    @Operation(summary = "드랍 테이블 조회", description = "drop table get")
    @GetMapping("/droptable")
    public ResponseEntity<?> getDropTable(@RequestParam int tableIdx) {
        logger.info("get drop table");

        try {
            List<DropTable> dropTableList = new ArrayList<>();
            if (tableIdx == 0) {
                dropTableList = gameService.getDropTable();
            }
            else {
                dropTableList = gameService.getTargetDropTable(tableIdx);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("result", dropTableList);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.toString());

            return new ResponseEntity<>(FAIL, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "경기 기록 저장", description = "game result save")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accessToken", value = "firebase 로그인 성공 후 " +
                    "발급 받은 accessToken",
                    required = true, dataType = "String", paramType = "header")
    })
    @PostMapping("/history")
    public ResponseEntity<?> saveHistory(@RequestBody HistoryRequest historyRequest,
                                         HttpServletRequest httpServletRequest)
            throws FirebaseAuthException {
        logger.info("post hisotry");

        try {
            String uid = TokenUtils.getInstance()
                    .getUid(httpServletRequest.getHeader("accessToken"));

            historyRequest.setUid(uid);

            gameService.saveHistory(historyRequest);

            return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.toString());

            return new ResponseEntity<>(FAIL, HttpStatus.BAD_REQUEST);
        }
    }




}
