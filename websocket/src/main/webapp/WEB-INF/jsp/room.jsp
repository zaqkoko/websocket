<!-- JSP 페이지에 대한 정보를 page 디렉티브의 속성들을 사용해서 정의한다.
표현식 : < %@ page 속성 %>  출처 : https://hyeonstorage.tistory.com/73 -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <meta charset="UTF-8">
    <title>WebSocket-ChattingRoom</title>
    <style>
        *{
            margin:0;
            padding:0;
        }
        .container{
            width: 500px;
            margin: 0 auto;
            padding: 100px
        }
        .container h1{
            text-align: left;
            padding: 5px 5px 5px 15px;
            color: #7babd0;
            margin-bottom: 10px;
        }
        .roomContainer{
            background-color: #ECE7E7;
            width: 500px;
            height: 500px;
            overflow: auto;
        }
        .roomList{
            border: none;
        }
        .roomList th{
            border: 1px solid #7babd0;
            background-color: #fff;
            color: #7babd0;
        }
        .roomList td{
            border: 1px solid #7babd0;
            background-color: #fff;
            text-align: left;
            color: #7babd0;
        }
        .roomList .num{
            width: 75px;
            text-align: center;
        }
        .roomList .room{
            width: 350px;
        }
        .roomList .go{
            width: 71px;
            text-align: center;
        }
        button{
            background-color: #7babd0;
            font-size: 14px;
            color: #000;
            border: 1px solid #000;
            border-radius: 5px;
            padding: 3px;
            margin: 3px;
        }
        .inputTable th{
            padding: 5px;
        }
        .inputTable input{
            width: 330px;
            height: 25px;
        }
    </style>
</head>

<script type="text/javascript">
    var ws;

    // window.onload = 페이지의 모든 요소가 로드된 이후 호출되게끔 해준다. $(document).ready와 비슷하다.
    window.onload = function(){
        getRoom();
        createRoom();
    }

    // 방의 정보를 가져온다.
    function getRoom(){
        commonAjax('/getRoom', "", 'post', function(result){
            createChatingRoom(result);
        });
    }

    function createRoom(){
        // 방만들기 버튼 눌렀을 때
        $("#createRoom").click(function(){
            // roomName id값에 입력한 값을 msg에 넣음
            var msg = {	roomName : $('#roomName').val()	};

            commonAjax('/createRoom', msg, 'post', function(result){
                createChatingRoom(result);
            });

            $("#roomName").val("");
        });
    }

    // 방 이동
    function goRoom(number, name){
        location.href="/moveChating?roomName="+name+"&"+"roomNumber="+number;
    }

    // 방만들기 & 방정보 가져오기
    function createChatingRoom(res){

        if(res != null){
            var tag = "<tr><th class='num'>순서</th><th class='room'>방 이름</th><th class='go'></th></tr>";
            res.forEach(function(d, idx){
                var rn = d.roomName.trim();
                var roomNumber = d.roomNumber;
                tag += "<tr>"+
                    "<td class='num'>"+(idx+1)+"</td>"+
                    "<td class='room'>"+ rn +"</td>"+
                    "<td class='go'><button type='button' onclick='goRoom(\""+roomNumber+"\", \""+rn+"\")'>참여</button></td>" +
                    "</tr>";
            });
            // roomList의 id값의 요소 내용을 삭제후 tag 추가
            $("#roomList").empty().append(tag);
        }
    }

    // 최초 페이지 접근시 ajax를 통해 방의 정보를 가져온다.
    function commonAjax(url, parameter, type, calbak, contentType){
        $.ajax({
            url: url,
            data: parameter,
            type: type,
            contentType : contentType!=null?contentType:'application/x-www-form-urlencoded; charset=UTF-8',
            success: function (res) {
                calbak(res);
            },
            error : function(err){
                console.log('error');
                calbak(err);
            }
        });
    }
</script>
<body>
<div class="container">
    <h1>웹소켓 채팅방</h1>
    <div id="roomContainer" class="roomContainer">
        <table id="roomList" class="roomList"></table>
    </div>
    <div>
        <table class="inputTable">
            <tr>
                <th>방 제목</th>
                <th><input type="text" name="roomName" id="roomName"></th>
                <th><button id="createRoom">방 만들기</button></th>
            </tr>
        </table>
    </div>
</div>
</body>
</html>