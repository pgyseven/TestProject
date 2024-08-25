
use pgy;
-- 회원 테이블 생성
CREATE TABLE `pgy`.`member` (
  `userId` VARCHAR(8) NOT NULL,
  `userPwd` VARCHAR(200) NOT NULL,
  `userName` VARCHAR(12) NULL,
  `mobile` VARCHAR(13) NULL,
  `email` VARCHAR(50) NULL,
  `registerDate` DATETIME NULL DEFAULT now(),
  `userImg` VARCHAR(45) NOT NULL DEFAULT 'avatar.png',
  PRIMARY KEY (`userId`),
  UNIQUE INDEX `mobile_UNIQUE` (`mobile` ASC) VISIBLE,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE);

-- 회원 테이블 수정(회원 포인트 점수 컬럼 부여)
ALTER TABLE `pgy`.`member` 
ADD COLUMN `userPoint` INT NULL DEFAULT 100 AFTER `userImg`;

-- DB 서버의 현재날짜와 현재 시간을 출력하는 쿼리문;
select now();

select md5('1234'); -- 암호화 기법 여러가지중 md5 쓴거임
select sha1('1234');

select sha1(md5('1234')); -- 실무에서는 이런식으로 두번 암호화 함

-- Member 테이블에 회원을 insert 하는 쿼리문
insert into member(userId, userPwd, userName, mobile, email) values(?, sha1(md5(?)), ?, ?, ?);

-- userId 로 해당 유저의 정보를 검색하는 쿼리문
select * from member where useId = ?;

-- member 테이블의 모든 회원 정보 검색하는 쿼리문
select * from member;

-- userId 가  ?인 회원 삭제 (회원 탈퇴)
delete from member where userId = ?;

-- dooly 라는 회원의 이메일을 수정하는 쿼리문
update member set email = 'dooly@dooly.com' where userId = 'dooly'; 
-- 데이터 선택 안되었다 에러드면 스키마 이름을 pgy.member 이렇게 use pgy; !

-- dooly 회원이 전화번호를 변결할 때 쿼리문 업데이트 쿼리문은 경우의 수가 많다 모바일일 수 도 있고 아닐수 도
update member set mobile = ? where userId = ?; 


-- 계층형 게시판 생성
CREATE TABLE `pgy`.`hboard` (
  `boardNo` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(20) NOT NULL,
  `content` VARCHAR(2000) NULL,
  `writer` VARCHAR(8) NULL,
  `postDate` DATETIME NULL DEFAULT now(),
  `readCount` INT NULL DEFAULT 0,
  `ref` INT NULL DEFAULT 0, -- 부모글의 글번호
  `step` INT NULL DEFAULT 0,
  `refOrder` INT NULL DEFAULT 0,
  PRIMARY KEY (`boardNo`),
  INDEX `hboard_member_fk_idx` (`writer` ASC) VISIBLE,
  CONSTRAINT `hboard_member_fk`
    FOREIGN KEY (`writer`)
    REFERENCES `pgy`.`member` (`userId`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION)
COMMENT = '계층형 게시판';



-- 계층형 게시판에 모든 게시글을 가져오는 쿼리문
select * from hboard order by boardNo desc;

-- 계층형 게시판에 게시글을 등록하는 쿼리문
insert into hboard(title, content, writer)
values('아싸~~ 1등이다.', '내용 무....', 'dooly');

insert into hboard(title, content, writer)
values('금산에 살얼이', '죽고 죽어 일백번', 'kildong');

insert into hboard(title, content, writer)
values(?, ?, ?);

-- 유저에게 지급되는 포인트를 정의한 테이블 생성
CREATE TABLE `pgy`.`pointdef` (
  `pointWhy` VARCHAR(20) NOT NULL,
  `pointScore` INT NULL,
  PRIMARY KEY (`pointWhy`))
COMMENT = '유저에게 적립할 포인트에 대한 저의 테이블,\n어떤 사유로 몇 포인트를 지급하는지에 대해 정의';

-- pointdef 테이블의 기초 데이터
INSERT INTO `pgy`.`pointdef` (`pointWhy`, `pointScore`) VALUES ('회원가입', '100');
INSERT INTO `pgy`.`pointdef` (`pointWhy`, `pointScore`) VALUES ('로그인', '1');
INSERT INTO `pgy`.`pointdef` (`pointWhy`, `pointScore`) VALUES ('글작성', '10');
INSERT INTO `pgy`.`pointdef` (`pointWhy`, `pointScore`) VALUES ('댓글작성', '2');
INSERT INTO `pgy`.`pointdef` (`pointWhy`, `pointScore`) VALUES ('게시글신고', '-10');


ALTER TABLE `pgy`.`pointdef` 
ADD COLUMN `pointdefNo` INT NOT NULL AUTO_INCREMENT FIRST,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`pointdefNo`);
;

--  유저의 포인트 적립 내역을 기록하는 pointlog 테이블 생성
CREATE TABLE `pgy`.`pointlog` (
  `pointLogNo` INT NOT NULL AUTO_INCREMENT,
  `pointWho` VARCHAR(8) NOT NULL,
  `pointWhen` DATETIME NULL DEFAULT now(),
  `pointWhy` VARCHAR(20) NOT NULL,
  `pointScore` INT NOT NULL,
  PRIMARY KEY (`pointLogNo`),
  CONSTRAINT `pointdef_member_fk`
    FOREIGN KEY (`pointWho`)
    REFERENCES `pgy`.`member` (`userId`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
COMMENT = '어떤 유저에게 어떤 사유로 몇 포인트가 언제 지급 되었는지를 기록하는 테이블 ';

-- 계층형 게시판 글 삭제 쿼리문
delete from hboard where boardNo=11;

-- 포인트 지급 log 저장하는 쿼리문 
insert into pointlog(pointWho, pointWhy, pointScore) values(?, ?, (select pointScore from pointdef where pointWhy = ?));

-- 유저에게 지급된 point를 update하는 쿼리문
update member set userpoint = userpoint + (select pointScore from pointdef where pointWhy = '글작성') where userId = ?;

-- 게시글의 첨부파일을 저장하는 테이블 생성
CREATE TABLE `pgy`.`boardimg` (
  `boardImgNo` INT NOT NULL AUTO_INCREMENT,
  `newFileName` VARCHAR(50) NOT NULL,
  `originalFileName` VARCHAR(50) NOT NULL,
  `ext` VARCHAR(4) NULL,
  `size` INT NULL,
  `boardNo` INT NOT NULL,
  `base64Img` TEXT NULL,
  INDEX `board_boardNo_fk_idx` (`boardNo` ASC) VISIBLE,
  PRIMARY KEY (`boardImgNo`),
  CONSTRAINT `board_boardNo_fk`
    FOREIGN KEY (`boardNo`)
    REFERENCES `pgy`.`hboard` (`boardNo`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
COMMENT = '게시판에 업로드 되는 업로드 파일을 기록하는 테이블';

-- 게시글 첨부 파일 테이블 수정
ALTER TABLE `pgy`.`boardimg` 
ADD COLUMN `thumbFileName` VARCHAR(60) NULL AFTER `originalFileName`;

-- 첨부 파일 테이블 이름 변경
ALTER TABLE `pgy`.`boardimg` 
RENAME TO  `pgy`.`boardupfiles` ;

 -- 컬럼명 변경
 ALTER TABLE `pgy`.`boardupfiles` 
CHANGE COLUMN `boardImgNo` `boardUpFileNo` INT NOT NULL AUTO_INCREMENT ;

-- 컬럼 크기 수정
ALTER TABLE `pgy`.`boardupfiles` 
CHANGE COLUMN `ext` `ext` VARCHAR(20) NULL DEFAULT NULL ;

-- 방금 insert 된 글의 글번호를 가져오는 쿼리문
select max(boardNo) from hboard;

-- 유저가 게시글을 저장할때 파일 업로드 하는 쿼리문
insert into boardupfiles(newFileName, originalFileName, thumbFileName, ext, size, boardNo, base64Img)
values(?, ?, ?, ?, ?, ?, ?);

-- 게시판 상세 페이지를 출력하는 쿼리문
select * from hboard where boardNo = 16;

select * from boardUpfiles where boardno=13; -- 이건 두번이라 속도에는 조금 느릴수 있다 2번 처리되니깐 뷰단으로 각각 가져가야해서 아우터 조인으로 하면 한번에

select * from member where userid = (select writer from hboard where boardNo = 16); -- 이렇게하면 맵 사용안하고 보드업파일스 맴버 조인 브이오를 채워서 사용 하는 버전에서는 이거 세개 사용

-- 게시글과 첨부 파일,  작성자 정보까지 함께 출력해보자(  조인 테이블 3개 3 - 1개의 조인조건이 나와야 한다.
select h.boardNo, h.title, h.content, h.writer, h.postDate, h.readCount
, f.*, m.userName, m.email 
from hboard h left outer join boardupfiles f
on h.boardNo = f.boardNo
inner join member m
on h.writer = m.userId
where h.boardNo = 41;
-- 참고로 여기서 select distinct h.boardNo, h.title, h.content, h.writer, h.postdate, h.readCount 이렇게 해도 중복은 제거 안된다.



-- 게시판 상세 페이지에서 그 게시글을 작성한 유저의 정보까지 출력 해 보자...


-- 부서명과 부서번호를 분리한 이유 데이터의 중복을 줄이기 위해서 equi(equal / =)Inner join사용 근데 이건 오라클만 가능 mysql은 ansi join
-- 서브쿼리로도 가능하다. 문제는 메인쿼리에서만 정보를 가져온다.
select *
from hboard inner join member
on hboard.writer = member.userid
where boardNo = 13;

select hboard.boardNo, board.title
from hboard inner join member
on hboard.writer = member.userid
where boardNo = 13;

-- 위도 기니깐 별명
select h.boardNo, h.title, m.userid, m.username
from hboard h inner join member m
on h.writer = m.userid -- 조인 조건(조인이 되는 테이블에서 의미가 같은 컬럼)
where h.boardNo = 13;

-- 게시글과 첨부파일을 함께 출력해보자
select *
from hboard h inner join boardupfiles f
on h.boardno = f.boardno;

select *
from hboard h left outer join boardupfiles f
on h.boardno = f.boardno; -- 이렇게 하면 현재 hboar 즉 왼쪽에 있는 board 정보 안나오니 left outer join을 쓴다 양쪽 다는 full outer
-- 이런 기능은 바지를 고르면 그 바지와 연관된걸 보여준다. 

-- 오라클에서 아래 두번째 명령어는 mysql도 가능 매니저가 king인 사원들의 이름과 직급 출력하세요
-- self join 하니의 테이블을 2개의 테이블인 것처럼 조인하여 출력하는 것, (테이블 별칭을 별도로 줘서 2개의 테이블인 것처럼 해야한다.)
SELECT ename, job FROM emp WHERE mgr=(SELECT empno FROM emp WHERE ename='KING');
select e.ename, e.mgr
from emp m inner join emp e on m.empNo = e.mgr
where m.ename = 'king';

-- 사원의 급여와 급여등급을 출력하세요 ... (NON EQUI JOIN)
select e.ename, e.sal, s.grade
from emp e, salgrade s
where e.sal > s.losal and e.sal < s.hisal; -- 오라클만 가능 ex 회원등급 얼마 팔았으면 골드 등급 사실 쓸데 많이 없다.

-- 컬럼의 중복 제거 포인트 부여 여러번 했을때 
select distinct 컬럼명 from pointlog;


-- 게시글의 조회수 증가 쿼리문
update hboard set readCount = readCount + 1
where boardNo = ?;

-- 게시판 조회수 증가를 위한 테이블 생성
CREATE TABLE `pgy`.`boardreadlog` (
  `boardReadLogNo` INT NOT NULL AUTO_INCREMENT,
  `readWho` VARCHAR(130) NOT NULL,
  `readWhen` DATETIME NULL DEFAULT now(),
  `boardNo` INT NOT NULL,
  PRIMARY KEY (`boardReadLogNo`))
COMMENT = '게시글을 조회한 내역 기록';

-- 1) readwho가 '0:0:0:0:0:0:0:1' 이고, boardNo가 ?번인 데이터가 있는지 조회
select readWhen from boardreadlog where readwho = '0:0:0:0:0:0:0:1' and boardno = 40;

-- 2 ) 1)번에서 나온 결과가 null 이면, insert
insert into boardreadlog(readwho, boardNo) values('0:0:0:0:0:0:0:1', 40);
insert into boardreadlog(readwho, boardNo) values(?, ?);
-- 3) 1)번에서 나온 결과가 null이 아니면... 현재날짜 시간과 이전에 읽은 날짜 시간의 날짜 차이를 구해야 한다.
-- 1)번 + 3)번의 내용 subquery와 함수를 이용하면 아래의 한 문장으로 해결할 수 있다.
select ifnull(datediff(now(), (select readWhen from boardreadlog where readWho = '0:0:0:0:0:0:0:1' and boardNo = 40)), -1) as datediff;

select ifnull(datediff(now(), (select readWhen from boardreadlog where readWho = ? and boardno = ?)), -1) as datediff;


-- 4) 3번에서 나온 결과가 1이상이면 조회한 후 조회한 readWhen 을 현재 시간으로 update 시켜야 한다.
update boardreadlog set readWhen = now() where readWho = ? and boardNo = ?;
-- 복합키 프라이머리 키 두개이상 가능함 지금 위에서 계속 후랑 보드 넘버를 가져간다 이 두개를 복합키로! 복합키는 지우고 쓸때 리드후는 누구고 보드 넘은 누구다 이 두개가 항상 엔드로 묶여서 지우고 읽고 해야한다. 단 우리는 지금 테이블은 안고치겠다.


------------------------------------------------------------------------ 계층형 게시판으로 만들기 ---------------------------------------------------------------------------

-- 1) 기존 게시글의 ref 컬럽 값을 boardNo 값으로update(기존의 글들은 모두 부모글이기 때문)

-- 2) 앞으로 정장될 게시글에도 ref 컬럼 값을 boardNo 값으로 update
update hboard set ref = #{boardNo} where boardNo = #{boardNo};
update hboard set ref = ? where boardNo = ?;


-- 2-1) 부모글에 대한 다른 답글이 있는 상태에서, 부모글의 답글이 추가되는 경우, (자리 확보를 위해)기존의 답글의 refOrder 값을 수정해야 한다.
update hboard set refOrder = refOrder + 1
where ref = ? and refOrder > ? ;

-- 3) 부모글의 boardNo를 ref에, 부모글의 step +1 값을 step에, 부모글의 refOrder +1 값을 refOrder에 저장한다.
insert into hboard(title, content, writer, ref, step, refOrder)
values(?, ?, ?, ?, ?, ?);

------------------------------------------------------------------------ 계층판 삭제 작업 ---------------------------------------------------------------------------
-- hboard테이블에서 삭제한 글인지를 포함할 수 있는 컬럼을 추가한다.
ALTER TABLE `pgy`.`hboard` 
ADD COLUMN `isDelete` CHAR(1) NULL DEFAULT 'N' AFTER `refOrder`;

-- 1) 실제 파일을 하드디스크에서도 삭제해야 하므로, 삭제 하기 전에 해당글의 첨부파일 정보를 불러와야 한다.
select * from boardupfiles where boardNo = ? ;

-- 2)boardNo 번 글의 첨부 파일이 있다면 첨부파일을 삭제해야 한다.
delete from boardupfiles where boardNo = ?

-- 3) boardNo 번글을 삭제 처리 (delete 문을 ㅅ실행하면, 게층형 게시판 정렬을 위해 만들어 놓은  ref, step, refOrder 컬럼의 정보 또한 삭제 되기 떄문에
-- 실제로는  update 문을 수행한다. 그리고 삭제 처리된 boardNo 번 글을 접근하지 못하도록 한다.
update hboard set isDelete = 'Y', title = '', content=''
where boardNo = ? 

-- 4) view 단에서 지워진 파일에 접근 하지 못하도록 해야 한다.

------------------------------------------------------------------------ 계층판 삭제 작업 ---------------------------------------------------------------------------
-- 15번 글의 title,content 수정
update hboard
set title = ?, content = ?
where boardNo = 15;

------------------------------------------------------------------------ 게시글 수정 ---------------------------------------------------------------------------

-- 게시글 update 하는 쿼리문
update hboard set title = ?, content = ?
where boardNo = ?;

-- 첨부파일을 pk로 삭제하는 메서드
delete from boardupfiles where boardUpFileNo = ?

-------------------------------------------------------------------------  인기글 5개 가져오기 ---------------------------------------------------------------------------
use pgy;
-- 삭제되지 않은 글 중에서 조회수가 높은순, 최신글 순 5개 가져오기
select * from hboard where isDelete = 'N' order by readCount desc, boardNo desc limit 5;


-- ----------------------------------------------------------- 페이징 -------------
-- 페이징(paging) : 많은 데이터를 일정 단위로 끊어서 출력하는 기법
-- 페이징은 단순히 유저에게 데이터를 끊어서 보여주는 의미가 아니라, 많은 데이터를 한꺼번에 출력하지 않고 데이터를 끊어서 출력한다는 의미가 있다.

-- 최종적으로 mysql에서 페이징을 위해 필요한 쿼리문
SELECT * FROM hboard order by ref desc, refOrder asc limit 보여주기시작할rowIndex번호, 1페이징에보여줄글의갯수;

-- 1) 게시판의 전체 데이터 수를 출력하는 쿼리문
use webkgy;
SELECT COUNT(*) FROM hboard; -- 337

-- 2) 전체 페이지 수
-- 만약 1페이지당 보여줄 데이터의 갯수가 10개라고 가정한다면
-- 1)번에서 나온 결과를 10으로 나누었을 때 몫이 페이지 수가 되는데, 나머지가 나온다면 +1을 한다.
-- 전체 페이지 수 : 337/10 = 33.7 -> 전체 페이지수 34 페이지
-- 전체 페이지 수 : 전체 데이터 수 / 1페이지 당 보여줄 글의 갯수 => 나누어 떨어진다면 몫 .... 나누어떨어지지 않는다면 몫+1


-- 3) ?번 페이지에서 보여주기 시작할 글의 index번호를 구하는 것이 핵심
-- 1페이지 번호 : 0 / 2페이지 번호 : 10 / 3페이지 번호 : 20
-- (현재 페이지 번호 - 1) * 한 페이지 당 보여줄 글의 갯수 => ?번 페이지에서 보여주기 시작할 글의 index 번호

-------------------------------------------- 페이징 블록 만들기 ---------------------------------------------
-- 1) 1개 페이징 블럭에서 보여줄 페이지 수 : 10

-- 1-1) 현재 페이지가 속한 페이징 블록의 번호
-- 현재 페이지 번호 / 1개의 페이징 블록에서 보여줄 페이지 수
-- 나누어 떨어지지 않으면 올림(+1)
-- 나누어 떨어지면 그 값

-- ex) 7 / 10 => 나누어 떨어지지 않으므로 1번 블록
-- ex) 14 / 10 => 나누어 떨어지지 않으므로 2번 블록
-- ex) 30 / 10 => 나누어 떨어지므로 3번 블록

-- 2) 현재 페이징 블록에서 출력 시작할 페이지 번호 : 
-- => (현재 페이징 블록 번호 - 1) * 1개 페이징 블럭에서 보여줄 페이지 수 + 1;
-- 7 페이지라면 -> (1 - 1) * 10 + 1 = 1
-- 14 페이지라면 -> (2 - 1) * 10 + 1 = 11
-- 30 페이지라면 -> (3 - 1) * 10 + 1 = 21

-- 3) 2)번에서 나온 값 + 1개 페이징 블럭에서 보여줄 페이지 수 - 1 

--------------------------------------게시물 검색 기능 구현----------------------------------

use pgy;

-- like 검색과 함께 사용하는 와일드 카드
-- 1) % : 몇자라도~
-- 2) _ : 한글자
-- sql 디벨로퍼
 -- job이 manager 인 모든 사원 정보를 검색 잡의 정보는 다 대문자이다.
 select from emp where job = 'MANAGER'; -- 소문자면 안나온다 아니면 upper('manager'); 이렇게 써야할거다 이게 대문자 바꿔주는거 반대는 lower
  select from emp where lower(job) = 'manaer'; --이렇게 컬럼 전체를 소문자로 바꿀수도 있다.
  
  select * form emmp where job like 'M%';
  
  
  SELECT * FROM OrderDetails where orderId like '%7_'; -- 끝에서 두번째 자리 가 7인 글자 (w3school 에서 해봄 https://www.w3schools.com/mysql/trymysql.asp?filename=trysql_select_all)
  
  -- like 검색을 이요하여 2월에 입5사한 사람을 검색
  select * from emp
  where hiredata like '___02%';


-- 검색어가 있을때 게시물 데이터 수를 얻어오는 쿼리문
-- 제목으로 검색
select count(*) from hboard where title like '%data%';

-- 작성자로 검색
select count(*) from hboard where writer like '%do%';

-- 내용으로 검색
select count(*) from hboard where content like '%금산%';



----------------
-- 제목으로 검색
select * from hboard where title like '%data%' order by ref desc, refOrder asc limit 0, 10;



-- 작성자로 검색
select * from hboard where writer like '%do%' order by ref desc, refOrder asc limit 0, 10;

-- 내용으로 검색
select * from hboard where content like '%금산%' order by ref desc, refOrder asc limit 0, 10;




--------------------------  회원 가입 기능 구현 ------------------------------
use pgy;

-- 회원 아이디가 중복되는 여부
select count(*) from member where userId = 'dooly';


-- 회원 테이블 수정
ALTER TABLE `pgy`.`member` 
ADD COLUMN `gender` VARCHAR(1) NOT NULL AFTER `userName`;

ALTER TABLE `pgy`.`member` 
CHANGE COLUMN `userImg` `userImg` VARCHAR(45) NULL DEFAULT 'avatar.png' ;

ALTER TABLE `pgy`.`member` 
ADD COLUMN `hobby` VARCHAR(60) NULL AFTER `email`;
member

-- 멤버 테이블에 회원 가입 -- userImg 멤버가 값이 null이 아닐때
-- 프로필 파일을 올렸을때
insert into member(userId, userPwd, userName, gender, mobile, email, hobby, userImg)
values(?, sha1(md5(?)), ?, ?, ?, ?, ?, ?);
-- 프로필 파일을 올리지 않았을 때 -- userImg 멤버가 값이 null
insert into member(userId, userPwd, userName, gender, mobile, email, hobby)
values(?, sha1(md5(?)), ?, ?, ?, ?, ?);


--------------------------  로그인 기능 구현 ------------------------------
-- 로그인 : 이사림이 이 권한을 실행할 때 필요한 자격이 있는지 없는지를 구분하는 일종의 인증작업

-- 게시판 글 작성, 수정, 삭제, 답글달기, 댓글 달기, 좋아요, 사용자 페이지... 는 로그인 한 유저만 가능하다.

-- 로그인시 필요한 쿼리문
select count(*) from member where userId = 'douner' and userPwd = sha1(md5('1234'));
-- 이건 인터셉터를 이용할때 로그인 한 유저의 정보를 세션(session)객체에 저장하기 위해 유저의 모든 컬럼을 조회함
select * from member where userId = 'douner' and userPwd = sha1(md5('1234'));


-- ?번글의 작성자 얻어오는 쿼리문
select writer from hboard where boardNo = ?

------------------------ 자동 로그인 기능 구현 -----------------------------
-- 자동 로그인을 체크한 유저의 경우에 아래의 두 컬럼에 자동 로그인을 체크 했을때의 세션값과 만료 시간을 저장
-- 향후에 쿠키에 있는 자동 로그인 정보와 db의 아래 컬럼에 있는 자동 로그인 정보와 비교하여 맞을 때만 자동로그인 시켜야
ALTER TABLE `pgy`.`member` 
ADD COLUMN `sesid` VARCHAR(40) NULL AFTER `userPoint`,
ADD COLUMN `allimit` DATETIME NULL AFTER `sesid`;

-- 자동 로그인 정보를 db에 저장하는 쿼리문
update member set sesid=? , allimit=? where userId=?

-- 쿠키에 자동 로그인 한다고 저장되어 있을때 자동 로그인하는 쿼리문
select * from member where sesId = '쿠키에 저장된 sesId' and allimit > now();


---------------------------  댓글형 게시판 -------------------------------

ALTER TABLE `pgy`.`hboard` 
COMMENT ='계층형 게시판, 댓글 게시판';

-- 계층형 게시판과 댓글형 게시판의 테이블을 함계 사용하기 위해 만든 컬럼 4게시판을 구분하는 등으로 사용할 것임

ALTER TABLE `pgy`.`hboard` 
ADD COLUMN `boardType` VARCHAR(10) NULL AFTER `isDelete`;


-- 기존의 글들을 계층형 게시판(boasrdType = 'hboard')의 글이라고 업데이트, 댓글형 게시판 boardType = 'rboard'
update hboard set boardType = 'hboard' where boardNo <= 62;
update `pgy`.`hboard` set `boardType` = 'rboard' where (`boardNo` = 63 );

-- 조회수 처리 테이블 또한 boardType 추가
ALTER TABLE `pgy`.`boardreadlog` 
ADD COLUMN `boardType` VARCHAR(10) NULL AFTER `boardNo`;

-- 컨텐츠 사이즈를 키워주기 위해서  타입 변경 4gb 까지임
ALTER TABLE `pgy`.`hboard` 
CHANGE COLUMN `content` `content` LONGTEXT NULL DEFAULT NULL ;

---------
ALTER TABLE `pgy`.`boardreadlog` 
DROP COLUMN `boardType`;

------------------------- 댓글 기능 구현 ----------------------------
use pgy;

-- 댓글을 저장하는 테이블 생성
CREATE TABLE `pgy`.`replyboard` (
  `replyNo` INT NOT NULL AUTO_INCREMENT,
  `replyer` VARCHAR(8) NULL,
  `content` VARCHAR(200) NULL,
  `regDate` DATETIME NULL DEFAULT now(),
  `boardNo` INT NOT NULL,
  PRIMARY KEY (`replyNo`))
COMMENT = '댓글을 저장하는 테이블';

-- replyboard FK 설정
alter table replyboard
add constraint replyer_member_fk foreign key(replyer) references member(userId)
on delete cascade;
-- on delete set null 댓글에 아이디를 null 을 넣는다. 그대신 replyer가 null 가능이여야함
-- on delete cascade 회원 탈퇴하면 댓글도 같이 삭제

-- replyboard fk 설정
alter table replyboard
add constraint boardNo_board_fk foreign key(boardNo) references hboard(boardNo);


--  댓글 등록
insert into replyboard(replyer, content, boardNo) values('douner', '세 댓글 테스트입니다.! 1등', 68);
--  ? 번 글에 대한 모든 댓글을 얻어오는 
select * from replyboard where boardNo = ?;
select * from replyboard where boardNo = 63 and boardType = 'rboard';

-- ? 번 글에 대한 게시글과 모든 댓글을 함께 얻어오는 쿼리문
select h.*, r.* from hboard h inner join replyboard r 
on h.boardNo = r.boardNo where h.boardNo = 63;

-- 모든 게시글과 모든 댓글을 함께 얻어오는 쿼리문
select h.*, r.*
from hboard h left outer join replyboard r 
on h.boardNo = r.boardNo 
where boardType = 'rboard';


-- ?번 글의 개수 얻어오기
select count(*) from replyboard where boardNo = 63;


-- 댓글 게시판의 데이터와, 그 댓글 게시물에 달려있는 댓글의 갯수를 함께 얻어오는 쿼리문
select h.boardNo, h.title, h.readcount, h.postDate, (select count(*) from replyBoard where r.boardNo = h.boardNo)
from hboard h left outer join replyboard r 
on h.boardNo = r.boardNo 
where boardType = 'rboard' 
group by h.boardNo
order by h.boardNo desc;

select * from hboard
where boardType = 'rboard';

SELECT h.boardNo, h.title, h.readcount, h.postDate, COUNT(r.boardNo) AS replyCount
FROM hboard h
LEFT JOIN replyboard r 
ON h.boardNo = r.boardNo
WHERE h.boardType = 'rboard'
GROUP BY h.boardNo
ORDER BY h.boardNo DESC;

-------------------------- 댓글 페이지 ----------------------
--  ? 번 글에 대한 모든 댓글을 얻어오는 
select * from replyboard where boardNo = ?;

select * from replyboard where boardNo = 62 order by replyNo desc limit 6,3;

select count(*) from replyboard where boardNo = ?;