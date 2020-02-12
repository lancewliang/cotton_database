#pragma comment(lib,"ws2_32.lib")
#include <stdio.h>  
#include <winsock2.h>
#include <string>
#include <stdlib.h>
#include <stdio.h>
#define MT4_EXPFUNC __declspec(dllexport) 
using  std::string;


#ifndef USE_H_
#define USE_H_
class CUser
{
public:
 CUser();
 virtual~ CUser();
 char* WcharToChar(const wchar_t* wp);
 char* StringToChar(const string& s);
 wchar_t* CharToWchar(const char* c);
 wchar_t* StringToWchar(const string& s);
 void Release();
private:
 char* m_char;
 wchar_t* m_wchar;
};
#endif;
CUser::CUser()
:m_char(NULL)
,m_wchar(NULL)
{
}
CUser::~CUser()
{
 Release();
}
char* CUser::WcharToChar(const wchar_t* wp)
{
 Release();
 int len= WideCharToMultiByte(CP_ACP,0,wp,wcslen(wp),NULL,0,NULL,NULL);
 m_char=new char[len+1];
 WideCharToMultiByte(CP_ACP,0,wp,wcslen(wp),m_char,len,NULL,NULL);
 m_char[len]='\0';
 return m_char;
}
wchar_t* CUser::CharToWchar(const char* c)
{
 Release();
 int len = MultiByteToWideChar(CP_ACP,0,c,strlen(c),NULL,0);
 m_wchar=new wchar_t[len+1];
 MultiByteToWideChar(CP_ACP,0,c,strlen(c),m_wchar,len);
 m_wchar[len]='\0';
 return m_wchar;
}
void CUser::Release()
{
 if(m_char)
 {
  delete m_char;
  m_char=NULL;
 }
 if(m_wchar)
 {
  delete m_wchar;
  m_wchar=NULL;
 }
}
char* CUser::StringToChar(const string& s)
{
 return const_cast<char*>(s.c_str());
}
wchar_t* CUser::StringToWchar(const string& s)
{
 const char* p=s.c_str();
 return CharToWchar(p);
}



MT4_EXPFUNC int _stdcall GetPlus(WCHAR* year, WCHAR* hour){
	
	WORD wVersionRequested;  
    WSADATA wsaData;  
    int err;  
	CUser u;
  
    wVersionRequested = MAKEWORD( 1, 1 );  
  
    err = WSAStartup( wVersionRequested, &wsaData );  
    if ( err != 0 ) {  
		return 88;  
    }  
  
    if ( LOBYTE( wsaData.wVersion ) != 1 ||  
        HIBYTE( wsaData.wVersion ) != 1 ) {  
            WSACleanup( );  
			return 99;  
    }  
    SOCKET sockClient=socket(AF_INET,SOCK_STREAM,0);  
  
    SOCKADDR_IN addrSrv;  
    addrSrv.sin_addr.S_un.S_addr=inet_addr("127.0.0.1");  
    addrSrv.sin_family=AF_INET;  
    addrSrv.sin_port=htons(6000);  
    connect(sockClient,(SOCKADDR*)&addrSrv,sizeof(SOCKADDR)); 

	u.WcharToChar(year);

	//	char yearchar[100];
//	itoa(121,yearchar,10); 
//	char hourchar[100];
//	itoa(hour, hourchar, 10);
	char buf[1024] = "";
	string empty("_");
//	string temp = year + empty + hour;
//	const char*cyear = year.c_str();
//	const char*chour = hour.c_str();
//	strcpy(buf, string("123").data());
	strcat(buf, u.WcharToChar(year));
	strcat(buf, empty.data());
	strcat(buf, u.WcharToChar(hour));
//	strcpy(buf, year);
    send(sockClient,buf,strlen(buf)+1,0);  
    char recvBuf[100];  
    recv(sockClient,recvBuf,100,0);  
//    printf("%s\n",recvBuf);  
//	string resultString(recvBuf);
    closesocket(sockClient);  
    WSACleanup();  

//	char *spar = new char[10];
//	strcpy(spar, "Test");	

	/*
	char aa[10];
	int total = strlen(recvBuf);
	int position = 0;
	for(int i=0;i<total;i++){
		if(recvBuf[i] == '_'){
			position = i;
			break;
		}
	}
	char* count = new char[5];
	strncpy(count, recvBuf, position);
	int length = atoi(count);
	
    char *final = new char[length];
	strncpy(final, &recvBuf[position + 1], length - position+2);
	return final;
	*/
	int r = atoi(recvBuf);
	return r;
}




