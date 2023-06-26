package com.example.newfieldpasser.service;

import com.example.newfieldpasser.dto.Response;
import com.example.newfieldpasser.dto.WishBoardDTO;
import com.example.newfieldpasser.entity.Board;
import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.exception.board.BoardException;
import com.example.newfieldpasser.exception.board.ErrorCode;
import com.example.newfieldpasser.repository.BoardRepository;
import com.example.newfieldpasser.repository.MemberRepository;
import com.example.newfieldpasser.repository.WishBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishBoardService {

    private final WishBoardRepository wishBoardRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final Response response;

    /*
    관심 글 등록
     */
    @Transactional
    public ResponseEntity<?> likeBoard(WishBoardDTO.WishBoardReqDTO wishPostReqDTO) {

        try {
            // 관심 글 등록 갯수 카운트
            String memberId  = wishPostReqDTO.getMemberId();
            long boardId = wishPostReqDTO.getBoardId();
            if (!wishBoardRepository.existsByMember_MemberIdAndBoard_BoardId(memberId, boardId)) { // 이미 좋아요 했을 시에 개수 카운트 하지 않음
                boardRepository.updateWishCount(boardId);
            }

            // 관심 글 등록
            Member member = memberRepository.findByMemberId(memberId).get();
            Board board = boardRepository.findByBoardId(boardId).get();
            wishBoardRepository.save(wishPostReqDTO.toEntity(member, board));

            return response.success("Register WishBoard Success!");

        } catch (BoardException e) {
            log.error("관심 글 등록 실패!");
            throw new BoardException(ErrorCode.REGISTER_WISH_BOARD_FAIL);
        }
    }

    /*
    관심 글 조회
     */
    public ResponseEntity<?> wishList(int page, Authentication authentication) {
        try {
            String memberId = authentication.getName();
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "registerDate")); // 관심 글 등록한 날짜 기준
            Slice<WishBoardDTO.WishBoardResDTO> result = wishBoardRepository.findByMemberId(memberId, pageRequest).map(WishBoardDTO.WishBoardResDTO::new);

            return response.success(result, "WishList Inquiry Success!");
        } catch (BoardException e) {
            log.error("관심 글 조회 실패!");
            throw new BoardException(ErrorCode.WISH_LIST_INQUIRY_FAIL);
        }
    }

    /*
    관심 글 삭제
     */
    @Transactional
    public ResponseEntity<?> deleteWishBoard(WishBoardDTO.WishBoardReqDTO wishPostReqDTO) {
        try {
            String memberId = wishPostReqDTO.getMemberId();
            long boardId = wishPostReqDTO.getBoardId();

            if (wishBoardRepository.existsByMember_MemberIdAndBoard_BoardId(memberId, boardId)) {
                boardRepository.minusWishCount(boardId);
            }

            wishBoardRepository.deleteByBoard_BoardIdAndMember_MemberId(boardId, memberId);

            return response.success("Delete WishBoard Success!");

        } catch (BoardException e) {
            log.error("관심 글 삭제 실패!");
            throw new BoardException(ErrorCode.WISH_BOARD_DELETE_FAIL);
        }
    }
}
