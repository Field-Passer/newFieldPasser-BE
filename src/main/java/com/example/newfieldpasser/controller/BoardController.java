package com.example.newfieldpasser.controller;

import com.example.newfieldpasser.dto.BoardDTO;
import com.example.newfieldpasser.dto.DistrictDTO;
import com.example.newfieldpasser.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /*
    게시글 등록
     */
    @PostMapping("/board/register")
    public ResponseEntity<?> registerBoard(@RequestParam(value = "file", required = false) MultipartFile file,
                                           Authentication authentication,
                                           BoardDTO.BoardReqDTO boardReqDTO) {

        return boardService.registerBoard(file, authentication, boardReqDTO);
    }

    /*
    게시글 상세조회
     */
    @GetMapping("/detail/{boardId}")
    public ResponseEntity<?> boardInquiryDetail(@PathVariable long boardId,
                                                Authentication authentication) {
        // 상세조회 시 조회 수 카운트
        boardService.updateViewCount(boardId);

        return boardService.boardInquiryDetail(boardId, authentication);
    }

    /*
    게시글 수정
     */
    @PutMapping("/board/edit/{boardId}")
    public ResponseEntity<?> editBoard(@PathVariable long boardId,
                                       @RequestParam(value = "file", required = false) MultipartFile file,
                                       BoardDTO.BoardEditReqDTO boardEditReqDTO) {

        return boardService.editBoard(boardId,file,boardEditReqDTO);
    }

    /*
    게시글 삭제
     */
    @DeleteMapping("/board/delete/{boardId}")
    public ResponseEntity<?> deleteBoard(@PathVariable long boardId) {

        return boardService.deleteBoard(boardId);
    }

    /*
    게시글 리스트 조회
     */
    @GetMapping("/board/list/{page}")
    public ResponseEntity<?> boardListInquiry(@PathVariable int page) {

        return boardService.boardListInquiry(page);
    }

    /*
    게시글 리스트 조회 - 카테고리별
     */
    @GetMapping("/board/list/category/{categoryId}/{page}")
    public ResponseEntity<?> boardListInquiryByCategory(@PathVariable int categoryId,
                                                        @PathVariable int page) {

        return boardService.boardListInquiryByCategory(categoryId, page);
    }

    /*
    게시글 리스트 조회 - 지역별
     */
    @GetMapping("/board/list/district/{page}")
    public ResponseEntity<?> boardListInquiryByDistrict(@RequestBody DistrictDTO.DistrictReqDTO districtReqDTO,
                                                        @PathVariable int page) {

        return boardService.boardListInquiryByDistrict(districtReqDTO, page);
    }

    /*
    게시글 리스트 조회 - 카테고리 + 지역
     */
    @GetMapping("/board/list/category-district/{categoryId}/{page}")
    public ResponseEntity<?> boardListInquiryByCategoryAndDistrict(@PathVariable int categoryId,
                                                                   @RequestBody DistrictDTO.DistrictReqDTO districtReqDTO,
                                                                   @PathVariable int page) {

        return boardService.boardListInquiryByCategoryAndDistrict(categoryId, districtReqDTO, page);
    }

    /*
    게시글 리스트 제목으로 검색
     */
    @GetMapping("/board/search-title/{page}")
    public ResponseEntity<?> boardListInquiryByTitle(@RequestParam(name = "title") String title,
                                                     @PathVariable int page) {

        return boardService.boardListInquiryByTitle(title, page);
    }

    /*
    게시글 리스트 제목 + 카테고리로 검색
     */
    @GetMapping("/board/search-title-category/{categoryId}/{page}")
    public ResponseEntity<?> boardListInquiryByTitleAndCategory(@RequestParam(name = "title") String title,
                                                                @PathVariable int categoryId,
                                                                @PathVariable int page) {

        return boardService.boardListInquiryByTitleAndCategory(title, categoryId, page);
    }

    /*
    게시글 리스트 제목 + 지역으로 검색
     */
    @GetMapping("/board/search-title-district/{page}")
    public ResponseEntity<?> boardListInquiryByTitleAndDistrict(@RequestParam(name = "title") String title,
                                                                @RequestBody DistrictDTO.DistrictReqDTO districtReqDTO,
                                                                @PathVariable int page) {

        return boardService.boardListInquiryByTitleAndDistrict(title, districtReqDTO, page);
    }

    /*
    게시글 리스트 제목 + 카테고리 + 지역으로 검색
     */
    @GetMapping("/board/search-title-category-district/{categoryId}/{page}")
    public ResponseEntity<?> boardListInquiryByTitleAndCategoryAndDistrict(@RequestParam(name = "title") String title,
                                                                           @PathVariable int categoryId,
                                                                           @RequestBody DistrictDTO.DistrictReqDTO districtReqDTO,
                                                                           @PathVariable int page) {

        return boardService.boardListInquiryByTitleAndCategoryAndDistrict(title, categoryId, districtReqDTO, page);
    }

    /*
    게시글 리스트 날짜 + 제목 + 카테고리 + 지역으로 검색
     */
    @GetMapping("/board/date/search-title-category-district/{categoryId}/{page}")
    public ResponseEntity<?> boardListInquiryByDateAndTitleAndCategoryAndDistrict(@RequestParam(name = "title") String title,
                                                                                  @RequestParam(name = "startTime") String startTime,
                                                                                  @RequestParam(name = "endTime") String endTime,
                                                                                  @RequestBody DistrictDTO.DistrictReqDTO districtReqDTO,
                                                                                  @PathVariable int categoryId,
                                                                                  @PathVariable int page) {

        return boardService.boardListInquiryByDateAndTitleAndCategoryAndDistrict(title,startTime,endTime,categoryId,districtReqDTO,page);
    }

    /*
    게시글 리스트 날짜 + 제목 + 카테고리로 검색
     */
    @GetMapping("/board/date/search-title-category/{categoryId}/{page}")
    public ResponseEntity<?> boardListInquiryByDateAndTitleAndCategory(@RequestParam(name = "title") String title,
                                                                       @RequestParam(name = "startTime") String startTime,
                                                                       @RequestParam(name = "endTime") String endTime,
                                                                       @PathVariable int categoryId,
                                                                       @PathVariable int page) {

        return boardService.boardListInquiryByDateAndTitleAndCategory(title,startTime,endTime,categoryId,page);
    }

    /*
    게시글 리스트 날짜 + 제목 + 지역으로 검색
     */
    @GetMapping("/board/date/search-title-district/{page}")
    public ResponseEntity<?> boardListInquiryByDateAndTitleAndDistrict(@RequestParam(name = "title") String title,
                                                                       @RequestParam(name = "startTime") String startTime,
                                                                       @RequestParam(name = "endTime") String endTime,
                                                                       @RequestBody DistrictDTO.DistrictReqDTO districtReqDTO,
                                                                       @PathVariable int page) {

        return boardService.boardListInquiryByDateAndTitleAndDistrict(title,startTime,endTime,districtReqDTO,page);
    }

    /*
    게시글 리스트 날짜 + 제목으로 검색
     */
    @GetMapping("/board/date/search-title/{page}")
    public ResponseEntity<?> boardListInquiryByDateAndTitle(@RequestParam(name = "title") String title,
                                                            @RequestParam(name = "startTime") String startTime,
                                                            @RequestParam(name = "endTime") String endTime,
                                                            @PathVariable int page) {

        return boardService.boardListInquiryByDateAndTitle(title,startTime,endTime,page);
    }

    /*
    게시글 리스트 날짜만으로 검색
     */
    @GetMapping("/board/date/search/{page}")
    public ResponseEntity<?> boardListInquiryByDate(@RequestParam(name = "startTime") String startTime,
                                                    @RequestParam(name = "endTime") String endTime,
                                                    @PathVariable int page) {

        return boardService.boardListInquiryByDate(startTime, endTime, page);
    }

    /*
    게시글 리스트 날짜 + 카테고리로 검색
     */
    @GetMapping("/board/date/search-category/{categoryId}/{page}")
    public ResponseEntity<?> boardListInquiryByDateAndCategory(@RequestParam(name = "startTime") String startTime,
                                                               @RequestParam(name = "endTime") String endTime,
                                                               @PathVariable int categoryId,
                                                               @PathVariable int page) {

        return boardService.boardListInquiryByDateAndCategory(startTime, endTime, categoryId, page);
    }

    /*
    게시글 리스트 날짜 + 지역(중복 선택)으로 검색
     */
    @GetMapping("/board/date/search-district/{page}")
    public ResponseEntity<?> boardListInquiryByDateAndCategory(@RequestParam(name = "startTime") String startTime,
                                                               @RequestParam(name = "endTime") String endTime,
                                                               @RequestBody DistrictDTO.DistrictReqDTO districtReqDTO,
                                                               @PathVariable int page) {

        return boardService.boardListInquiryByDateAndDistrict(startTime, endTime, districtReqDTO, page);
    }

    /*
    게시글 리스트 날짜 + 카테고리 + 지역(중복 선택)으로 검색
     */
    @GetMapping("/board/date/search-category-district/{categoryId}/{page}")
    public ResponseEntity<?> boardListInquiryByDateAndCategory(@RequestParam(name = "startTime") String startTime,
                                                               @RequestParam(name = "endTime") String endTime,
                                                               @RequestBody DistrictDTO.DistrictReqDTO districtReqDTO,
                                                               @PathVariable int categoryId,
                                                               @PathVariable int page) {

        return boardService.boardListInquiryByDateAndCategoryAndDistrict(startTime, endTime, districtReqDTO, categoryId, page);
    }

    /*
    게시글 리스트 검색 - 동적 쿼리 사용
    날짜 + 제목 + 카테고리 + 지역
     */
    @GetMapping("/search/{page}")
    public ResponseEntity<?> searchBoard(@RequestParam(name = "title", required = false) String title,
                                         @RequestParam(name = "categoryName", required = false) String categoryName,
                                         @RequestParam(name = "startTime", required = false, defaultValue = "1900-01-01T00:00:00") String startTime,
                                         @RequestParam(name = "endTime", required = false, defaultValue = "9999-12-31T23:59:59") String endTime,
                                         @RequestParam(name = "districtNames", required = false) List<String> districtNames,
                                         @PathVariable int page) {

        return boardService.searchBoard(title, categoryName, startTime,endTime, districtNames, page);
    }


    @PutMapping("/board/sold-out/{boardId}")
    public ResponseEntity<?> changeSoldOut(@PathVariable long boardId) {
        return boardService.changeSoldOut(boardId);
    }


      /*
    회원 닉네임 누르면 회원 정보 표시
     */
    @GetMapping("/member-inquiry/{boardId}/{page}")
    public ResponseEntity<?> boardByMemberInquiry(@PathVariable long boardId , @PathVariable int page){
        return boardService.boardByMemberInquiry(boardId,page);
    }

}
