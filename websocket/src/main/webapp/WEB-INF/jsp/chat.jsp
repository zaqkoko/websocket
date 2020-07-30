<!-- JSP 페이지에 대한 정보를 page 디렉티브의 속성들을 사용해서 정의한다.
표현식 : < %@ page 속성 %>  출처 : https://hyeonstorage.tistory.com/73 -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<!-- JSON이란? - JavaScript Object Notation의 약자로, 브라우저와 서버 사이에서 오가는 데이터의 형식이다. -->

<!DOCTYPE html>
<html>
<head>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <meta charset="UTF-8">
    <title>WebSocket-Chatting</title>
    <!-- CSS -->
    <style>
        *{
            margin:0;
            padding:0;
        }
        /* 채팅방 container */
        .container{
            width: 500px;
            margin: 0 auto;
            padding: 100px;
        }
        /* 상단 h1 */
        .container h1{
            text-align: left;
            padding: 5px 5px 5px 15px;
            color: #7babd0;
            margin-bottom: 10px;
        }
        /* 채팅방 배경 */
        .chating{
            background-color: #bad3e6;
            width: 500px;
            height: 500px;
            overflow: auto;
        }
        /* 채팅 폰트 */
        .chating .me{
            color: #000;
            text-align: right;
        }

        .chating .others{
            color: #000;
            text-align: left;
        }
        /* 채팅 입력 */
        input{
            width: 390px;
            height: 25px;
        }
        /* 메세지칸 숨겨두기 */
        #myMsg{
            display: none;
        }
    </style>
</head>

<!-- 참고 사이트 : https://developer.mozilla.org/ko/docs/WebSockets/Writing_WebSocket_client_applications -->

<script type="text/javascript">
    var ws; // 웹소켓 담을 변수 선언

    // 닉네임 입력하면 wsOpen() 호출
    function wsOpen(){

        // 새로운 웹소켓 오브젝트를 생성하여 ws url을과 chating + 방번호값 넣고 접속.
        // 반환값 ws 오브젝트의 ws.readystate의 값은 CONNECTING 이며, 연결이 도면 OPEN으로 변경된다.
        ws = new WebSocket("ws://" + location.host + "/chating/" + $("#roomNumber").val());
        console.log(ws);

        // wsEvt() 호출
        wsEvt();
    }

    function wsEvt() {

        /* 연결 시도 중 에러가 발생하면 ErrorEvent가 WebSocket 오브젝트로 전달되고, 그로 인해 onerror 핸들러가 실행됨.
           그 후에 연결이 종료되는 이유를 가리키는 CloseEvent 이벤트가 WebSocket 오브젝트로 전달돼 onclose 핸들러가 실행됨.
           아직 연결 끊기, 에러는 구현 X  */

        // WebSocket이 연결되면 호출되는 이벤트
        // 소켓이 열리면 세팅 초기화
        ws.onopen = function(data){

        }

        // 메세지가 수신되면 message 이벤트가 onmessage 함수로 전달되게된다.
        // 수신된 데이터를 <div> 요소에 넣는다.
        ws.onmessage = function(data) {

            var msg = data.data;

            // 만약 msg가 null이 아니고 msg의 좌우 공백을 제거 했을때 공백이 아니라면
            if (msg != null && msg.trim() != '') {

                // 서버에서 데이터를 JSON 형태로 전달해주기 때문에 받은 데이터를 JSON.parse를 활용하여 파싱을 한다.
                // JSON.parse() 메소드는 JSON 문자열의 구문을 분석하고, 그 결과에서 JS의 값이나 객체를 생성한다.
                var d = JSON.parse(msg);

                // 상대방과 자기자신을 구분하기 위해 구현

                // 파싱한 객체의 type값을 확인하여 getId면
                if (d.type == "getId") {

                    // 초기 설정된 값이므로 채팅방에 값을 입력하는 것이 아닌 추가한 태그 sessionId에 값을 세팅한다

                    // sessionId가 null이 아니라면 d.sessionId, null이라면 ""
                    var si = d.sessionId != null ? d.sessionId : "";

                    // 만약 공백이 아니라면
                    if (si != '') {

                        // SessionId 값에 sessionId값 넣어주기
                        $("#sessionId").val(si);
                    }
                }

                // 만약 파싱한 객체의 type값이 message라면
                else if (d.type == "message") {

                    // 최초 닉네임을 입력하고 연결됐을 때, 발급받은 sessionId 값을 비교하여 같다면 내가 보낸 것임.
                    // 만약 파싱한 객체의 sessionId 값이 sessionId 값과 같다면
                    if (d.sessionId == $("#sessionId").val()) {

                        // 나로 발송
                        $("#chating").append("<p class='me'>나 : " + d.msg + "</p>");

                        // 아니라면
                    } else {

                        // 그 외
                        $("#chating").append("<p class='others'>" + d.userName + " : " + d.msg + "</p>");
                    }
                } else {
                    console.warn("unknown type")
                }

                // chating id값의 마지막에 구문을 추가한다.
                // $("#chating").append("<p>" + msg + "</p>");
            }
        }

        // document.addEventListener = 이벤트가 발생했을 때 그 처리를 담당하는 함수를 가리키며 이벤트 핸들러라고도 부른다.
        //                             지정된 타입의 이벤트가 특정 요소에서 발생하면, 웹 브라우저는 그 요소에 등록된 이벤트 리스너를 실행시킨다.
        document.addEventListener("keypress", function(e){   // function(e) = (e)는 event관련 object를 받는 argument이다.

            // 엔터 누르면 send() 호출 (엔터 => 키코드 13)
            if(e.keyCode == 13){
                send();
            }
        });
    }

    // 연결 끊기
    function close() {
        ws.onclose;
        console.log("a");
        alert("연결이 끊어졌습니다");
    }

    // 닉네임 입력 안 하면 입력하라고 알림창
    function chatName(){

        // userName id값을 넣음
        var userName = $("#userName").val();

        // 만약 userName이 null이거나 공백이라면... trim() = 좌우 공백을 제거해줌. 가운데 들어간 공백은 제거안함.
        if(userName == null || userName.trim() == ""){

            // 닉네임 입력 알림창 이후 닉네임 입력 칸을 입력 상태로 만들어 줌.
            alert("닉네임을 입력해주세요.");
            $("#userName").focus();

        }else{

            // 닉네임 입력하면 wsOpen()호출(웹소켓)
            wsOpen();

            // 닉네임 입력 칸 숨기기, 메세지 칸 보여주기
            $("#myName").hide();
            $("#myMsg").show();
        }
    }

    // 메세지 발송
    function send() {

        // JSON 형태로 발신
        var option={
            type : "message", // type값을 message로 구분하여 발송
            roomNumber : $("#roomNumber").val(),
            sessionId : $("#sessionId").val(),
            userName : $("#userName").val(),
            msg : $("#chatting").val()
        }

        // userName, chatting id값을 각각 uN, msg 변수에 넣어줌.
        //var uN = $("#userName").val();
        //var msg = $("#chatting").val();

        // 서버에 데이터 전송(WebSocket 오브젝트의 send() 호출)
        // JSON.stringify(value, replacer, space) - value 필수(JSON문자열로 변환할 값 - 배열, 객체, 숫자, 문자 등이 될 수 있다.)
        ws.send(JSON.stringify(option))

        // 메세지 값 공백으로 바꿔주기.
        $('#chatting').val("");
    }
</script>
<body>
<div id="container" class="container">
    <h1>${roomName}의 채팅방</h1>
    <!-- 세션값을 저장하기 위한 input 태그 -->
    <input type="hidden" id="sessionId" value="">
    <input type="hidden" id="roomNumber" value="${roomNumber}">

    <div id="chating" class="chating">
    </div>

    <div id="myName">
        <table class="inputTable">
            <tr>
                <th>닉네임 </th>
                <th><input type="text" name="userName" id="userName" style="text-align: center" placeholder="채팅에 사용할 닉네임을 입력해주세요."></th>
                <th><button onclick="chatName()" id="startBtn">등록</button></th>
            </tr>
        </table>
    </div>
    <div id="myMsg">
        <table class="inputTable">
            <tr>
                <th>메세지</th>
                <th><input id="chatting" style="text-align: center" placeholder="보내실 메세지를 입력하세요."></th>
                <th><button onclick="send()" id="sendBtn">보내기</button></th>
            </tr>
            <tr>
                <td></td>
                <th><button onclick="close()" id="closeBtn">끊기</button></th>
            </tr>
        </table>
    </div>
</div>
</body>
</html>