package com.moniproj.dao;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.miniproj.model.HBoardDTO;
import com.miniproj.persistence.HBoardDAO;
import com.miniproj.service.hboard.HBoardService;


@RunWith(SpringJUnit4ClassRunner.class) // 단위 테스트 할 수 있는 라이브러리 test 패키지에서 Spring container(root context DAO DB 연결과 관련됌) 에 접근할 수 있도록 함
@ContextConfiguration( // root-context.xml 파일의 위치를 제공하여 파일에 접근할 수 있도록
		locations = { "file:src/main/webapp/WEB-INF/spring/**/root-context.xml" })
public class intsertDummyBoard {
	
	@Autowired
	private HBoardDAO dao; // 루트다시 컨택스트에 있음 빈스 그래프에 보면 보임 거기 떠 있으니 주입해 주는 것
	
	@Autowired
	private HBoardService service;
	
	@Test
	public void insertDummyDataToBoard() throws Exception { //더미 데이터 만들거임
		
		for (int i = 0; i < 300 ; i++) {
			HBoardDTO dto = HBoardDTO.builder()
			.title("dummy data" + i + "...")
			.content("dummy content" + i)
			.writer("dooly")
			.build();
			
		
			if (dao.insertNewBoard(dto) == 1) {
				
				int newBoardNo = dao.getMaxBoardNo();
				
				
				dao.updateBoardRef(newBoardNo);
		}
		
		}
		
	}
}
