package top.jessi.sample;

import java.io.File;
import java.io.IOException;
import top.jessi.nanohttpd.core.NanoHTTPD;


/**
 * Created by Jessi on 2023/5/17 17:42
 * Email：17324719944@189.cn
 * Describe：http服务
 */
public class HttpService extends NanoHTTPD {

    private final String mRootDirectory;

    public HttpService(int port, String rootDirectory) {
        super(port);
        this.mRootDirectory = rootDirectory;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        String fullPath = mRootDirectory + uri;
        // 检查文件是否存在
        File file = new File(fullPath);
        if (file.exists() && file.isFile()) {
            // 如果文件存在且是文件类型，则返回文件内容
            try {
                return responseByFile(session, file);
            } catch (IOException e) {
                e.printStackTrace();
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Internal Server Error");
            }
        } else if (file.exists() && file.isDirectory()) {
            // 如果文件存在且是目录类型，则返回目录下的文件列表
            File[] files = file.listFiles();
            if (files != null) {
                String htmlContent = generateDirectoryHtml(files);
                return newFixedLengthResponse(Response.Status.OK, "text/html", htmlContent);
            }
        }
        // 文件不存在或其他错误情况
        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "File Not Found");
    }

    private String generateDirectoryHtml(File[] files) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><ul>");
        // 上级目录链接
        sb.append("<li><a href=\"../\">..</a></li>");
        // 子目录和文件链接
        for (File file : files) {
            String fileName = file.getName();
            String link = file.isDirectory() ? fileName + "/" : fileName;
            sb.append("<li><a href=\"").append(link).append("\">").append(fileName).append("</a></li>");
        }
        sb.append("</ul></body></html>");
        return sb.toString();
    }
}

