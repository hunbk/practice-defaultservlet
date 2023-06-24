# Default Servlet이란

# 0. 주제
- 디폴트 서블릿이란?
- 디폴트 서블릿의 기능
- 디폴트 서블릿의 우선순위

# 1. 디폴트 서블릿이란?
[톰캣 공식문서](https://tomcat.apache.org/tomcat-9.0-doc/default-servlet.html#what)에서 다음과 같이 디폴트 서블릿을 설명하고 있다.

> 디폴트 서블릿은 **디렉토리 목록**을 제공할 뿐만 아니라 **정적 리소스**를 제공하는 서블릿입니다.(디렉토리 목록은 활성화된 경우만 제공)

> **정적 리소스란?**
> - 정적 리소스는 클라이언트가 요청 시 서버의 특정 디렉토리(톰캣에선 `webapp`)에 존재하는 파일을 변경 없이 그대로 서비스하는 것이다. (e.g: `html`, `css`, `js`, `image`)
> - `jsp` 파일의 경우 서버에서 동적으로 내용을 변경하기 때문에 동적 리소스이다.

즉, 디폴트 서블릿은 클라이언트가 요청 시 `webapp` 디렉토리 하위에 있는 **디렉토리 목록**과 **정적 리소스**를 찾아서 응답으로 돌려주는 서블릿이라는 것을 알 수 있다.

### **주의!**
**디폴트 서블릿은 디스패처 서블릿이 아니다.**
디스패처 서블릿은 스프링MVC에서 사용하는 프론트 컨트롤러 패턴이 적용된 서블릿이다.
**디폴트 서블릿은 정적 리소스를 제공하는 서블릿**이라는 점을 다시 상기하고 넘어가자.

# 2. 디폴트 서블릿의 기능
디폴트 서블릿은 클라이언트의 요청 uri를 분석하여 어떤 응답을 돌려줄 지 결정한다.
uri에 확장자가 포함되어 있다면 정적 리소스를, `/` 경로로 끝난다면 디렉토리 목록에 대한 요청으로 판단하여 이를 응답하는 방식이다.

- **디렉토리 목록 요청** : `http://localhost:8080/hello/`
- **정적 리소스 요청** : `http://localhost:8080/hello/hello1.html`

****************************디렉토리 구조****************************
기능을 알아보기 앞서 프로젝트의 디렉토리 구조는 다음과 같다.

```
main
├── java
│   └── com
│       └── example
│           └── HelloServlet.java
├── resources
└── webapp
    └── hello
        ├── hello.css
        ├── hello.js
        ├── hello1.html
        ├── hello2.html
        └── hello3.html
```

## 2.1 디렉토리 목록 제공

현재 `index.html`과 같은 환영 파일이 없기 때문에, 톰캣의 기본 설정이 적용된 상태에서 클라이언트가 `/` 경로로 요청할 경우 `404 에러 페이지`를 응답하게 된다.

- `http://localhost:8080/`

<img width="581" alt="Untitled" src="https://user-images.githubusercontent.com/52270259/235356601-39dfae6e-8135-4e2b-8f15-defaf879b132.png">

그렇기 때문에 디폴트 서블릿이 제공하는 기능 중 하나인 디렉토리 목록을 직접 확인해보기 위해서는 약간의 설정이 필요하다.

### 2.1.1 톰캣 설정

서블릿 컨테이너(톰캣)가 실행될 때, `web.xml` 파일에 정의된 내용을 읽어서 실행 환경을 설정한다.
톰캣을 설치하면 기본적으로 `web.xml` 파일이 존재하고, 톰캣이 실행될 때 이 내용을 기본값으로 사용한다.
다음 경로에서 `web.xml` 파일을 확인할 수 있다.

- `tomcat-9.0/conf/web.xml`

여기서는 간단하게 `DefaultServlet`의 기본 설정 정보만 확인해보자.

**tomcat/conf/web.xml**

```xml
<!-- 서블릿 정의 -->
<servlet>
    <servlet-name>default</servlet-name>
    <servlet-class>org.apache.catalina.servlets.DefaultServlet</servlet-class>
    <init-param>
        <param-name>debug</param-name>
        <param-value>0</param-value>
    </init-param>
    <init-param>
        <param-name>listings</param-name>
        <param-value>false</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>

<!-- 서블릿 매핑 -->
<servlet-mapping>
    <servlet-name>default</servlet-name>
    <!-- / 하위의 모든 경로로 매핑 -->
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

`<init-param>`에서 디폴트 서블릿을 생성할 때 매개변수를 설정할 수 있다.

- `debug`: 디버깅 수준(기본값: `0(해제)`, tomcat 엔지니어가 아니라면 유용하게 사용되지 않음)
- `listings`: index 파일이 없을 경우, 디렉토리 목록 표시. (기본값: `false`)
- 다른 매개변수 정보는 [톰캣 공식 문서](https://tomcat.apache.org/tomcat-9.0-doc/default-servlet.html#change)에서 확인할 수 있다.

### 2.1.2 설정 재정의

톰캣의 디폴트 서블릿 기본 설정이 디렉토리 목록을 보여주지 않기 때문에, 설정을 재정의할 필요가 있다.
[톰캣 공식 문서](https://tomcat.apache.org/tomcat-9.0-doc/default-servlet.html#where)에서는 프로젝트의 `webapp/WEB-INF/tomcat-web.xml` 에 설정을 재정의 하는 것을 권장한다.
`listings` `sortListings`를 `true`로 설정하여 디렉토리 목록을 표시하고, 이름순으로 정렬하도록 재정의하였다.

**webapp/WEB-INF/tomcat-web.xml**

```xml
<!-- 서블릿 정의 -->
<servlet>
    <servlet-name>default</servlet-name>
    <servlet-class>
        org.apache.catalina.servlets.DefaultServlet
    </servlet-class>

    <init-param>
        <param-name>debug</param-name>
        <param-value>0</param-value>
    </init-param>

    <!-- index 파일이 없을 경우, 디렉토리 목록 표시. [기본값: false] -->
    <init-param>
        <param-name>listings</param-name>
        <param-value>true</param-value>
    </init-param>

    <!-- 디렉토리의 목록 정렬. [기본값: false] -->
    <init-param>
        <param-name>sortListings</param-name>
        <param-value>true</param-value>
    </init-param>

    <load-on-startup>1</load-on-startup>
</servlet>

<!-- 서블릿 매핑 -->
<servlet-mapping>
    <servlet-name>default</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

여기까지 진행 시 클라이언트의 `/` 하위의 모든 요청에 대하여 디폴트 서블릿이 디렉토리 목록 기능을 제공한다.

- `localhost:8080/`

<img width="584" alt="Untitled 1" src="https://user-images.githubusercontent.com/52270259/235356674-252168bd-b2d1-466e-ab1b-e128a1182913.png">

- `localhost:8080/hello/`

<img width="583" alt="Untitled 2" src="https://user-images.githubusercontent.com/52270259/235356689-370bf755-8513-4001-aaed-a16e956c160f.png">

***

## 2.2 정적 리소스 제공
다음 경로에서 `DefaultServlet`을 확인할 수 있다.

- `tomcat-9.0/lib/catalina.jar/org/apache/catalina/servlets/DefaultServlet.class`

당연하지만, 디폴트 서블릿도 서블릿이기 때문에 `HttpServlet`을 상속받고 메서드를 재정의하거나 직접 구현하고 있다.
간단하게 `DefaultServlet`의 핵심 기능만 확인해보자.

**DefaultServlet.class**
```java
public class DefaultServlet extends HttpServlet {
    //...
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //doGet() 호출
        this.doGet(req, resp);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //serveResource() 호출
        this.serveResource(request, response, true, this.fileEncoding);
    }

    protected void serveResource(HttpServletRequest request, HttpServletResponse response, boolean content, String inputEncoding) throws IOException, ServletException {
        //클라이언트가 요청한 uri 경로를 path에 저장
        String path = this.getRelativePath(request, true);

        if (path.length() == 0) {
            this.doDirectoryRedirect(request, response);
        } else {
            //정적 리소스를 찾아서 응답하는 로직 실행
            WebResource resource = this.resources.getResource(path);
            //생략...
        }
    }
    //...
}
```

서블릿 컨테이너가 디폴트 서블릿의 `service()` 메서드를 호출하면 `doGet()` → `serveResource()` 순으로 호출된다.
디폴트 서블릿에서 정적 리소스를 제공하는 핵심 기능은 `serveResource()` 메서드가 담당한다.
`serveResource()` 메서드에는 대략 300라인의 로직이 작성되어 있어서 모두 보여줄 수 없지만, 핵심은 요청 uri를 통해 클라이언트가 요청하는 uri를 서버의 루트 디렉토리(webapp) 이하에서 찾아서 응답하는 기능을 수행한다.
만약 요청 uri에 해당하는 리소스가 존재하지 않을 경우 `404 에러 페이지`를 응답한다.

**리소스가 존재하지 않음**
- `http://localhost:8080/hello/존재X.html`

<img width="581" alt="Untitled 3" src="https://user-images.githubusercontent.com/52270259/235356705-6eeb4e17-91ae-47db-b53b-554a19684138.png">

**정적 리소스 응답**
- `http://localhost:8080/hello/hello1.html`

<img width="383" alt="Untitled 4" src="https://user-images.githubusercontent.com/52270259/235356714-8b621c71-c0e4-4051-bb93-dfe5384afe2d.png">

***

# 3. 디폴트 서블릿의 우선순위
디폴트 서블릿이 매핑되는 url 패턴을 `web.xml`에서 확인해보면 `/`로 매핑되는 것을 알 수 있다.

**web.xml**

```xml
<!-- 서블릿 매핑 -->
<servlet-mapping>
    <servlet-name>default</servlet-name>
    <!-- / 하위의 모든 경로로 매핑 -->
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

그렇기 때문에 클라이언트의 요청을 디폴트 서블릿이 먼저 받게 된다면, 모든 요청에 대해 정적 리소스를 응답하게 되는 문제가 발생할 수 있다.
따라서 서블릿 컨테이너에서 서블릿을 호출할 때, 디폴트 서블릿의 우선순위는 가장 낮게 설정된다.

정적 리소스와 동일한 경로로 매핑되는 서블릿이 존재할 경우, 해당 서블릿이 디폴트 서블릿보다 먼저 호출되는지 직접 확인해보자.
결과를 간단하게 확인하기 위해 임의로 `HelloServlet`을 생성하고 url 매핑을 `/hello/hello1.html` 로 설정하였다.

## **비교대상**

- **정적 리소스** : `webapp/hello/hello1.html`
<img width="133" alt="스크린샷 2023-05-02 오후 11 04 11" src="https://user-images.githubusercontent.com/52270259/235718502-16c5b8e5-a74f-4d03-bea5-c599ff308767.png">

- **HelloServlet**(`/hello/hello1.html`로 매핑)

```java
@WebServlet(name = "hello", urlPatterns = "/hello/hello1.html")
public class HelloServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("HelloServlet.doGet");
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        //HelloServlet 호출 출력
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>HelloServlet 호출</h1>");
        out.println("</body></html>");
    }
}
```

## 결과
<img width="366" alt="2" src="https://user-images.githubusercontent.com/52270259/235718592-b4bc8166-e3b0-467b-b227-33f7d6b0e5a5.png">

클라이언트가 `localhost:8080/hello/hello1.html`을 요청할 때, 디폴트 서블릿이 아닌 `HelloServlet`이 호출되는 것을 확인할 수 있다.

***

# 더 알아보기
- 디스패처 서블릿
- 스프링MVC의 리소스 핸들러

# 참조
- [Apache Tomcat 9 (9.0.74) - Default Servlet Reference](https://tomcat.apache.org/tomcat-9.0-doc/default-servlet.html)
- [conf/web.xml 과 WEB-INF/web.xml](https://soye0n.tistory.com/156)
- [리소스 핸들러 (default Servlet)](https://xxxelppa.tistory.com/330)
