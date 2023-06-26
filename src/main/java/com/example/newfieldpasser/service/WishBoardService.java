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
import org.springframework.http.ResponseEntity;
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
            log.error("게시글 좋아요 실패!");
            throw new BoardException(ErrorCode.REGISTER_WISH_BOARD_FAIL);
        }
    }
}
