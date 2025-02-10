package kr.hhplus.be.server.common.api.filter;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 필터에서 요청 본문을 읽기 위한 Custom HttpServletRequestWrapper
 *
 * ContentCachingRequestWrapper는 요청 본문을 메모리에 캐시하여 여러 번 읽을 수 있게 합니다.
 * 캐싱 시점: 컨트롤러나 필터에서 최초로 본문을 읽을 때 캐싱됩니다.
 * 데이터 접근: 캐싱된 데이터는 doFilter() 실행 후 필터에서 접근 가능합니다.
 * LogFilter 에서 requestWrapper.getContentAsByteArray() 호출 시 컨트롤러가 아직 본문을 읽지 않아 캐시가 비어 있습니다.
 */
public class CachedBodyRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] cachedBody;

    public CachedBodyRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.cachedBody = StreamUtils.copyToByteArray(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedServletInputStream(cachedBody);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    public byte[] getCachedBody() {
        return cachedBody;
    }

}
