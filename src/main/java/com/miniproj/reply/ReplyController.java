package com.miniproj.reply;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.miniproj.model.MyResponseWithData;
import com.miniproj.model.PagingInfoDTO;
import com.miniproj.model.ReplyVO;
import com.miniproj.model.ResponseType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reply")
@RequiredArgsConstructor
public class ReplyController {

	private final ReplyService rService;
	

	@GetMapping("/all/{boardNo}/{pageNo}") // @PathVariable : 이 자리에 boardNo가 들어간다는 의미
	public ResponseEntity getAllReplyByBoardNo(@PathVariable("boardNo") int boardNo, @PathVariable("pageNo") int pageNo ) {//@ResponseBody List<ReplyVO>
		//@ResponseBody 는 List<ReplyVO> 을 제이슨으로 만들라는 애이다.  단점이 전송 상태를 같이 못 보내준다.
		System.out.println(boardNo + "번의 모든 댓글을 얻어오자.");

	
		ResponseEntity result = null;
		Map<String, Object> data = null;
		
		try {
			
			;
			
			data = rService.getAllReplies(boardNo, new PagingInfoDTO(pageNo, 3));
			result = new ResponseEntity(MyResponseWithData.success(data), HttpStatus.OK);
		
			
		} catch (Exception e) {
		
			e.printStackTrace();
			
	
			result =  new ResponseEntity(MyResponseWithData.fail(), HttpStatus.BAD_REQUEST); //400에러 반환
		}
		return result;
		
	}
}
