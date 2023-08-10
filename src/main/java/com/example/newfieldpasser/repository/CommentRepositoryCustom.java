package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.dto.CommentDTO;
import com.example.newfieldpasser.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface CommentRepositoryCustom {
   Slice<CommentDTO.CommentResDTO> findByBoardId(Pageable pageable, long boardId);

   Optional<Comment> findCommentByIdWithParent(long commentId);
}
