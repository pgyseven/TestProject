package com.miniproj.controller.hboard;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.miniproj.model.BoardDetailInfo;
import com.miniproj.model.BoardUpFileStatus;
import com.miniproj.model.BoardUpFilesVODTO;
import com.miniproj.model.HBoardDTO;
import com.miniproj.model.HBoardVO;
import com.miniproj.model.HReplyBoardDTO;
import com.miniproj.model.MyResponseWithoutData;
import com.miniproj.model.PagingInfo;
import com.miniproj.model.PagingInfoDTO;
import com.miniproj.model.SearchCriteriaDTO;
import com.miniproj.service.hboard.HBoardService;
import com.miniproj.util.FileProcess;
//import java.lang.* //생략 java lang패키지는 기본 패키지
// Controller 단에서 해야 할 일
// 1) URI 매핑(어떤 URI가 어떤 방식으로(GET/Post)으로 호출 되었을 때 어떤 메서드에 매핑 시킬 것이냐
// 2) 있다면 view 단에서 보내준 매개변수 수집
// 3) 데이터베이스에 대한 CRUD 를 수행하기 위해 service단의 해당 메서드를 호출. service단에서 rerutn 갑을 view 바인딩 view 호출
// 4) 부가적으로... 컨트롤러 단은 servlet에 의해 동작되는 모듈이기 때문에 HttpServletRequest, HttpServletReponse,HttpSession
// 등의 Servlet 객체들을 이용할 수 있다 -> 이러한 객체들을 이용하여 구현할 기능이 있다면 그 기능은 Controller 단에서 구현한다.(참고로 리퀘스트는 한번 하면 사라지지만 세션은 유저마다 하나씩 생긴다. 유저가 떠날때까지 유지) 서비스단 이후는 서블릿이 아니라 리퀘스트 세션 불러올수 없다 그래서 컨트롤러 단에서만 이걸 불러오는건 다 해야한다.
import com.miniproj.util.GetClientIPAddr;

@Controller // 아래의 클래스가 컨트롤러 객체임을 명시
@RequestMapping("/hboard")
public class HBoardController {
	// Log를 남길 수 있도록 하는 객체
	private static Logger logger = LoggerFactory.getLogger(HBoardController.class);

	@Autowired
	private HBoardService service; // 상속 받았기 때문에 부모가 HBoard 다

	@Autowired
	private FileProcess fileProcess;

	// 아래서 지역변수에 의해 안날아가고 유저가 저장누르기 전까지 파일 정보 가지고 있게 하기 이걸 만든다.
	// 유저가 업로드한 파일을 임시 보관하는 객체(컬렉션)
	private List<BoardUpFilesVODTO> uploadFileList = new ArrayList<BoardUpFilesVODTO>(); // 이게 스테이틱하면 모든 객체가 공유하니깐 다른
																							// 사람도 올린것처럼 될수도???

	private List<BoardUpFilesVODTO> modifyFileList;

	// 게시판 전체 목록 리스트를 출력하는 메서드
	// defaultValue : pageNo 쿼리스트링 값이 생략되어 호출된다면 그 값이 1로 초기값이 부여되도록 함.(400 에러 방지 참고로 제대로된 파라미터를 안줄때 400 에러가 뜸 그래서 디폴트 벨류준거)
	@RequestMapping("/listAll")
	public void listAll(Model model, @RequestParam(value="pageNo", defaultValue = "1") int pageNo, @RequestParam(value="pagingSize", defaultValue = "10") int pagingSize, SearchCriteriaDTO searchCriteria) {
		logger.info(pageNo + "번 페이지 출력.....HBoardController.listAll()~" + ", 검색조건 :" + searchCriteria.toString());

		PagingInfoDTO dto = PagingInfoDTO.builder()
		.pageNo(pageNo)
		.pagingSize(pagingSize)
		.build();
		
		// 서비스 단 호출
		List<HBoardVO> list = null;
		Map<String, Object> result = null;
		try {
			result = service.getAllBoard(dto, searchCriteria);
			
			PagingInfo pi = (PagingInfo)result.get("pagingInfo"); // 명시적 변환 다운 캐스팅
			
			list = (List<HBoardVO>)result.get("boardList");
			
			model.addAttribute("boardList", list);
			model.addAttribute("pagingInfo",pi);
			model.addAttribute("search",searchCriteria);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("exception", "error");

		}

//      for(HBoardVO b : list) {
//			System.out.println(b.toString());
//		}
		// model.addAttribute("boardList", list); //바이터 파인딩
		// return "/hboard/listAll.jsp";
		// /hboard/listAll.jsp 으로 포워딩 됨
	}

	// 게시판 글 저장 페이지를 출력하는 메서드
	@RequestMapping("/saveBoard")
	public String showSaveBoardForm() {
		return "/hboard/saveBoardForm";
	}

	// 게시글 저장 버튼을 눌렀을때 해당 게시글을 db에 저장하는 메서드
	@RequestMapping(value = "/saveBoard", method = RequestMethod.POST) /* url 호출이름이 위와 같은데 전송방식이 다르기 때문에 문제 없다 */
	public String saveBoard(HBoardDTO boardDTO, RedirectAttributes redirectAttributes) {
		System.out.println("이게시글을 출력하자....................");

		boardDTO.setFileList(this.uploadFileList); // 첨부파일 리스트를 boardDTO에 주입

		System.out.println("이게시글을 출력하자...................." + boardDTO.toString()); // "/saveBoard 보이드는 이거에 의해서 .jsp 가
																					// 붙어서 간다.

		String returnPage = "redirect:/hboard/listAll";
		try {
			if (service.saveBoard(boardDTO)) { // 게시글 저장에 성공했다면
				redirectAttributes.addAttribute("status", "success");

			}
		} catch (Exception e) { // 게시글 저장에 실패했다면
			e.printStackTrace();
			redirectAttributes.addAttribute("status", "fail");

		}
		// VO는 리드 온리로서 디비에 꺼내온걸 저장해서 앞단까지 가져가는 것 즉 뷰단까지 즉 DTO와 VO가 다른거다 같이 쓰기도 하는데 앞단
		// 뷰단은 자주 바뀔수 있다. 그래서 따로 쓰는게 좋다.
		this.uploadFileList.clear();
		return returnPage; // 게시글 전체 목록 페이지로 돌아감
	}

	@RequestMapping(value = "/upfiles", method = RequestMethod.POST, produces = "application/json; charset=UTF-8;") // text/plain/
																													// application/json이걸보고
																													// 페이지
																													// 이동을
																													// 안해도
																													// 되겠구나
																													// 한다.
																													// /
																													// 요청처리를
																													// 제이슨으로
																													// 하겠다.
																													// produces
																													// 리퀘스트
																													// 매핑을
																													// 처리하는
																													// 방식
																													// 우린
																													// 제이슨으로
																													// 할거임
	public ResponseEntity<MyResponseWithoutData> saveBoardFile(@RequestParam("file") MultipartFile file,
			HttpServletRequest request) { // file 이라고 했는데 못찾을때가 있다 그래서 @RequestParam("file") 이걸 넣었다. / 컨트롤단 즉 서블릿단이다 여기는
											// 리퀘스트가 있는곳
		// ResponseEntity<> http status 서로 상태를 주고 받음 통신은~ 받을 준비 보낼준비까지도 요청의 성공 여부를 나타내는
		// 상태코드 참고로 나중에 하겠지만 레스트 방식은 제이슨 형태고 주고 받음
		// MultipartFile file 이건 컨트롤 단에서만 작동함
		System.out.println("파일 전송됨... 이제 저장해야함...");

		ResponseEntity<MyResponseWithoutData> result = null;

		try {

			BoardUpFilesVODTO fileInfo = fileSave(file, request);

			// System.out.println("저장된 파일의 정보" + fileInfo.toString());

			this.uploadFileList.add(fileInfo); // 맴버변수라서 버릇처럼 강사님이 하시는 것 지역변수와 매개변수를 구문 잘하면 상관 없다.

			// 7월 17일 가장 먼저 해야 할 코드 : front에서 업로드한 파일을 지웠을때 백엔드에서도 지워야 한다.
			System.out.println("===================================================");
			System.out.println("현재 파일 리스트에 있는 파일들");
			for (BoardUpFilesVODTO f : this.uploadFileList)
				System.out.println(f.toString());
			System.out.println("===================================================");

			String tmp = null;
			if (fileInfo.getThumbFileName() != null) {
				// 이미지
				tmp = fileInfo.getThumbFileName();
			} else {
				tmp = fileInfo.getNewFileName().substring(fileInfo.getNewFileName().lastIndexOf(File.separator) + 1);
			}

			MyResponseWithoutData mrw = MyResponseWithoutData.builder().code(200).msg("success").newFileName(tmp)
					.build();

			// 저장된 새로운 파이르 이름을 json으로 return 시키도록
			result = new ResponseEntity<MyResponseWithoutData>(mrw, HttpStatus.OK); // 이넘타입(컨트롤 스페이스 누르면 뜨는 아이콘)은 점 찍고
																					// 나오는 상수의 값만 받을수 있는
			// BoardUpFilesVODTO 이걸 제이슨으로 바꿔서

		} catch (IOException e) {

			e.printStackTrace();
			result = new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		return result;

	}

	private BoardUpFilesVODTO fileSave(MultipartFile file, HttpServletRequest request) throws IOException {
		// 파일의 기본정보 가져옴
		String contentType = file.getContentType(); // 마인 타입?
		String originalFileName = file.getOriginalFilename();
		long fileSize = file.getSize();

		byte[] upfile = file.getBytes();// 2진파일의 파일 내용 즉 실제 파일 IO익셉션 뜸 하드디스크 내용을 읽을때 하드디스크가 말성이면 못 읽을테니~ 예외처리 여기서 못하니 에드
										// 뜨로우로 컨트롤단으로 보낸다.
		// 저장될 파일의 실제 contents 즉 파일의 실제 데이터

		String realPath = request.getSession().getServletContext().getRealPath("/resources/boardUpFiles"); // 지금들어온 요청에서
																											// 가각의 세션에서
																											// 우리
																											// 프로젝트(프로젝트
																											// 여러개를 서버에서
																											// 다 돌릴 수
																											// 있으니깐)객체를
																											// 얻어오고 그것의
																											// 리얼패스 즉
																											// 각각의 서블릿은
																											// 리얼패스가 다를
																											// 수 있다.
		// boardUpFiles 서버가 실행되면 서버에 만들어진다. 리퀘스트 객체에서 얻어와야하는데 리퀘스트 객체는 컨트롤단에서만 움직인다 컨트롤은
		// 서블릿객체와 스프링이 동시 작동하는 공간이기에
		// 실제파일 저장(이름변경, base64, thumbnail)
		BoardUpFilesVODTO fileInfo = fileProcess.saveFileToRealPath(upfile, realPath, contentType, originalFileName,
				fileSize); // 지역변수라서 파일 여러개 올리면 오버라이드 된다.
		return fileInfo;
	}

	@RequestMapping(value = "/removefile", method = RequestMethod.POST, produces = "application/json; charset=UTF-8;")
	public ResponseEntity<MyResponseWithoutData> removeUpFile(@RequestParam("removedFileName") String removeFileName,
			HttpServletRequest request) {
		System.out.println("업로드된 파일을 삭제하자 : " + removeFileName);

		boolean removeResult = false;

		ResponseEntity<MyResponseWithoutData> result = null;

		int removeIndex = -1;

		// 넘겨져온 removeFileName과 uploadFileList 배열의 originalFileName과 같은 것이 있는지 체크하여 삭제처리
		// 해야 함
		for (int i = 0; i < this.uploadFileList.size(); i++) { // this.uploadFileList 컬렉션이라 랭스가 없다 java.api를 보고 참고하면
																// size 가 있다.
			if (uploadFileList.get(i).getNewFileName().contains(removeFileName)) {
				// 지금 contains를 쓴건 이퀠스를 안쓴 이유는 지금 2024\07\14\파일이름.확장자 와 뉴파일 이름만 파일이름.확장자만 가지고
				// 비교해서 삭제해야하니 파일 이름만 포함된거에서 삭제하느라
				System.out.println(i + "번째에서 해당 파일 찾았음 : " + uploadFileList.get(i).getNewFileName()); // 파일 4개 올렸을때 두번째꺼
																										// 삭제하면 : 2번째에서
																										// 해당 파일 찾았음 !
																										// \2024\07\17\BoardDAO.java

				String realPath = request.getSession().getServletContext().getRealPath("/resources/boardUpFiles");

				if (fileProcess.removeFile(realPath + uploadFileList.get(i).getNewFileName())) { // 지금 역슬라이스 붙일 필요 없는게
																									// 년월일 만들때 앞에다가
																									// 만들어둬서
					removeIndex = i;
					removeResult = true;
					break; // 포문 안에 if문안에다가 삭제를 넣으면 size만큼 돌으라고 했는데 하나가 비면 어리둥절한 상황이라 에러날 수 있다 그러니 포문 다 돌고
							// 밖에서! 삭제해라

				}
			}
		}
		if (removeResult) { // 하드디스크에서 파일 삭제
			uploadFileList.remove(removeIndex);

			System.out.println("====================================================================================");
			System.out.println("현재 파일 리스트에 있는 파일들");
			for (BoardUpFilesVODTO f : this.uploadFileList) {
				System.out.println(f.toString());
			}
			System.out.println("=====================================================================================");

			result = new ResponseEntity<MyResponseWithoutData>(new MyResponseWithoutData(200, "", "success"),
					HttpStatus.OK);
		} else {
			result = new ResponseEntity<MyResponseWithoutData>(new MyResponseWithoutData(400, "", "fail"),
					HttpStatus.CONFLICT);
		}

		return result;

	}

	@RequestMapping(value = "/cancelBoard", method = RequestMethod.GET, produces = "application/json; charset=UTF-8;") // 제이슨으로
																														// 반환하기에
																														// ,
																														// produces
																														// =
																														// "application/json;
																														// charset=UTF-8;"
																														// 이거
																														// 꼭
																														// 써야함
	public ResponseEntity<MyResponseWithoutData> cancelBoard(HttpServletRequest request) {
		System.out.println("유저가 업로드 한 모든 파일을 삭제하자!");
		String realPath = request.getSession().getServletContext().getRealPath("/resources/boardUpFiles");
		if (this.uploadFileList.size() > 0) { // new로 호출했기에 주소값은 있어서 널은 무조건 아니기에 널하고 비교 불가

			allUploadFileDelete(realPath, this.uploadFileList);
			this.uploadFileList.clear(); // 리스트에 있는 모든 데이터 삭제 / 반복 횟수에 영향을 줄 수있는 행동이니 삭제는 이렇게 밖에서 항상 기억 해라
		}

		return new ResponseEntity<MyResponseWithoutData>(new MyResponseWithoutData(200, "", "success"), HttpStatus.OK);

	}

	private void allUploadFileDelete(String realPath, List<BoardUpFilesVODTO> fileList) { // 위에 재사용된 코드를 리팩토링 함
		for (int i = 0; i < fileList.size(); i++) { // 반복문이 다돌면 파일이 다 삭제
			fileProcess.removeFile(realPath + fileList.get(i).getNewFileName());

			// 이미지 파일이면 썸네일 파일또한 삭제 해야함
			if (fileList.get(i).getThumbFileName() != null || fileList.get(i).getThumbFileName() != "") {
				fileProcess.removeFile(realPath + fileList.get(i).getThumbFileName());
			}

		}
	}

	@RequestMapping("/removeBoard")
	public String removeBoard(@RequestParam("boardNo") int boardNo, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		System.out.println(boardNo + "번들을 삭제 하자");
		// DAO 단에서 해당 BoardNo번 글을 삭제 처리한 후
		try {

			List<BoardUpFilesVODTO> fileList = service.removeBoard(boardNo);
			// 빈파일 리스트를 확인한다고 fileList == null 이라고 하면 무조건 널이 아님이다 왜 냐면 비어있지만 객체즉
			// boardupfilevodto형태의 객체는 무조건 보내니깐 여기선 객체가 잇냐 없냐로 물어본거나 다름 없으니 무조건 널이 아님이다 객체는
			// 꼭 돌려주니깐

			String realPath = request.getSession().getServletContext().getRealPath("/resources/boardUpFiles");

			// 첨부파일이 있다면 , 첨부파일의 정보를 가져와 하드디스크에서도 첨부파일을 삭제 해야한다.
			if (fileList.size() > 0) {
				allUploadFileDelete(realPath, fileList);
			}

			redirectAttributes.addAttribute("status", "success");
		} catch (Exception e) {

			e.printStackTrace();
			redirectAttributes.addAttribute("status", "fail");
		}
		return "redirect:/hboard/listAll";

	}

	// 아래의 viewBoard()는 /viewBoard(게시글 상세보기), /modifyBoard(게시글을 수정하기 위해 게시글을 불러오는)
	// 일때 2번 호출된다.
	@RequestMapping(value = { "/viewBoard", "/modifyBoard" }) // value 컨트롤 클릯해서 보면 String[] value() default {}; 이렇게 보이는데
																// 배열 형태로 할 수 있단걸 알 수 있다.
	public String viewBoard(@RequestParam("boardNo") String boardNo, Model model, HttpServletRequest request) {
		String returnViewPage = "";
		List<BoardDetailInfo> boardDetailInfo = null; // service.read(boardNo, ipAddr);
		
		try {
		String ipAddr = GetClientIPAddr.getClientIp(request);

		System.out.println(ipAddr + " 가 뒤에 번호 글 조회" + boardNo);

		// System.out.println("uri : " + request.getRequestURI());

		

		

		
			if (request.getRequestURI().equals("/hboard/viewBoard")) {
				System.out.println("상세보기 호출...");
				returnViewPage = "/hboard/viewBoard";
				boardDetailInfo = service.read(Integer.parseInt(boardNo), ipAddr);
			} else if (request.getRequestURI().equals("/hboard/modifyBoard")) {
				System.out.println("수정하기 호출...");
				returnViewPage = "/hboard/modifyBoard";
				boardDetailInfo = service.read(Integer.parseInt(boardNo));

				int fileCount = -1;
				for (BoardDetailInfo b : boardDetailInfo) {
					fileCount = b.getFileList().size();
					this.modifyFileList = b.getFileList(); // db 에서 가져온 업로드된 파일리스트를 멤버변수에 할당
				}
				model.addAttribute("fileCount", fileCount);

				outputAny();
			}

		 } catch (SQLException e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	         returnViewPage = "redirect:/hboard/listAll?status=fail";
	      } catch (Exception ex) {
	         System.out.println(boardNo + " 의 값");
	         System.out.println("요기~~~~~~~~~~~~~~~~~" + ex.getMessage());
	         returnViewPage = "redirect:/hboard/listAll?status=fail";
	      }

	      model.addAttribute("boardDetailInfo", boardDetailInfo);

	      return returnViewPage;

	   }

	private void outputAny() {
		System.out.println("====================================================================================");
		System.out.println("수정하기 호출 리스트에 있는 파일들");
		for (BoardUpFilesVODTO file : this.modifyFileList) {

			System.out.println(file.toString());
		}
		System.out.println("=====================================================================================");
	}

	public String showReplyForm() {
		return "/hboard/replyForm";
	}

	@RequestMapping(value = "/saveReply", method = RequestMethod.POST)
	public String saveReplyBoard(HReplyBoardDTO replyBoard, RedirectAttributes redirectAttributes) { // HBoardDTO 를사용해도
																										// 된다 첨부파일 리스트는
																										// null로 되도 되니깐
		System.out.println(replyBoard + "답글로 저장하자");

		String returnPage = "redirect:/hboard/listAll"; // 리다이렉트라서 답글 저장 성공도 뜬다.

		try {
			if (service.saveReply(replyBoard)) {
				redirectAttributes.addAttribute("status", "success");

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			redirectAttributes.addAttribute("status", "fail");
		}

		return returnPage;
	}

//	@RequestMapping("/modifyBoard")
//	public void modifyBoard(@RequestParam("boardNo") int boardNo, HttpServletRequest request) {
//		
//		System.out.println("uri : " + request.getRequestURI());
//		
//	} 컨트롤러단에 메서드를 함께 썼기에 삭제

	@RequestMapping(value = "/modifyRemoveFileCheck", method = RequestMethod.POST, produces = "application/json; charset=UTF-8;")
	public ResponseEntity<MyResponseWithoutData> modifyRemoveFileCheck(
			@RequestParam("removeFileNo") int removedFilePK) {
		System.out.println(removedFilePK + " 파일을 삭제 처리 하자");
		// 아직 게시판이 최종 수정이 될지 안될지 모르는 상태이기 때문에 파일을 하드에서 삭제 할 수가 없다.
		// 삭제될 파일을 삭제한다고 체크만 해두고, 나중에 게시판이 최중 수정이되면 그때 실제 삭제처리 해야한다.

		for (BoardUpFilesVODTO file : this.modifyFileList) {
			if (removedFilePK == file.getBoardUpFileNo()) {
				file.setFileStatus(BoardUpFileStatus.DELETE);
			}

		}

		outputAny();

		return new ResponseEntity<MyResponseWithoutData>(new MyResponseWithoutData(200, null, "success"),
				HttpStatus.OK);

	}

	@RequestMapping(value = "/cancelRemoveFile", method = RequestMethod.POST, produces = "application/json; charset=UTF-8;")
	public ResponseEntity<MyResponseWithoutData> cancelRemoveFile() {
		System.out.println("파일리스트의 모든 파일을 삭제 취소 처리");

		for (BoardUpFilesVODTO file : this.modifyFileList) {
			file.setFileStatus(null);
		}
		outputAny();
		return new ResponseEntity<MyResponseWithoutData>(new MyResponseWithoutData(200, null, "success"),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/modifyBoardSave", method = RequestMethod.POST)
	public String modifyBoardSave(HBoardDTO modifyBoard, @RequestParam("modifyNewFile") MultipartFile[] modifyNewFile,
			HttpServletRequest request, RedirectAttributes rediAttributes) {
		System.out.println(modifyBoard.toString() + "로 수정하자");
		
		try { // 서버나 파일 저장시에도 하나라도 안되면 예외 처리로 나오게 범위를 넓혔다 
			for (int i = 0; i < modifyNewFile.length; i++) {
				System.out.println("새로 업로드된 파일 :" + modifyNewFile[i].getOriginalFilename());

				BoardUpFilesVODTO fileInfo = fileSave(modifyNewFile[i], request);
				fileInfo.setFileStatus(BoardUpFileStatus.INSERT); // insert 되어야 할 파일임을 기록
				this.modifyFileList.add(fileInfo);
			}

			outputAny();
			//DB에 저장(servixe 호출)
			modifyBoard.setFileList(modifyFileList);
			if(service.modifyBoard(modifyBoard)) {
				rediAttributes.addAttribute("status", "success");
			}
		} catch (Exception e) { //DB의 익셉션 및 IO 익셉션을 모두 포함하기 위하여  익셉션으로 바꿈
			
			e.printStackTrace();
			rediAttributes.addAttribute("status", "fail");
		}
		return "redirect:/hboard/viewBoard?boardNo=" + modifyBoard.getBoardNo();

	}

}
