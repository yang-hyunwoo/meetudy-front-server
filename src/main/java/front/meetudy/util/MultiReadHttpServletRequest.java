package front.meetudy.util;


import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.*;


/**
 * HttpServletRequest의 요청 바디(InputStream)를 캐싱해서
 * 여러 번 읽을 수 있도록 허용하는 유틸 클래스
 */
@Slf4j
public class MultiReadHttpServletRequest extends HttpServletRequestWrapper {
    private ByteArrayOutputStream cachedBytes;

    public MultiReadHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    /**
     * 최초 호출 시 super.getInputStream()을 읽어서 cachedBytes에 저장
     * 이후엔 캐시된 byte 배열을 재사용
     * @return
     * @throws IOException
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (cachedBytes == null)
            cacheInputStream();

        return new CachedServletInputStream(cachedBytes.toByteArray());
    }

    /**
     * getInputStream()을 기반으로 텍스트 리더 생성
     * JSON, XML 등 텍스트 형태일 경우 여기서 읽음
     * @return
     * @throws IOException
     */
    @Override
    public BufferedReader getReader() throws IOException{
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    /**
     * 요청 바디를 한 번 읽어서 메모리에 저장
     * IOUtils.copy()를 통해 안전하게 ByteArrayOutputStream에 복사
     * @throws IOException
     */
    private void cacheInputStream() throws IOException {
        cachedBytes = new ByteArrayOutputStream();
        try (InputStream is = super.getInputStream()) {
            IOUtils.copy(is, cachedBytes);
        }
    }

    /**
     * 로깅,디버깅 유용
     * @return
     * @throws IOException
     */
    public String getCachedBodyAsString() throws IOException {
        if (cachedBytes == null) cacheInputStream();
        return cachedBytes.toString(UTF_8);
    }

    private static class CachedServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream buffer;

        public CachedServletInputStream(byte[] contents) {
            this.buffer = new ByteArrayInputStream(contents);
        }

        @Override
        public int read() {
            return buffer.read();
        }

        @Override
        public boolean isFinished() {
            return buffer.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        /**
         * 서블릿 비동기(non-blocking) I/O 처리 시 필요한 메서드
         * @param listener The non-blocking IO read listener
         *
         */
        @Override
        public void setReadListener(ReadListener listener) {
            log.warn("setReadListener() is not supported in MultiReadHttpServletRequest.");
        }
    }
}