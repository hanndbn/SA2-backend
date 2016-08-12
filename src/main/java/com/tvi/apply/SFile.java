package com.tvi.apply;

import com.tvi.common.type.AccessDeny;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;

public class SFile extends HttpServlet {

    private static final int DEFAULT_BUFFER_SIZE = 10240; // ..bytes = 10KB.
    private static final long DEFAULT_EXPIRE_TIME = 604800000L; // ..ms = 1 week.
    private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";
    private String basePath;

    @Override
    public void init() throws ServletException {
        this.basePath = new File(getServletContext().getRealPath("/")).getParentFile().getAbsolutePath() + "/hr-files/";
        this.basePath = this.basePath.replace('\\', '/');
        if (this.basePath == null) {
            throw new ServletException("FileServlet init param 'basePath' is required.");
        } else {
            File path = new File(this.basePath);
            if (!path.exists()) {
                throw new ServletException("FileServlet init param 'basePath' value '"
                        + this.basePath + "' does actually not exist in file system.");
            } else if (!path.isDirectory()) {
                throw new ServletException("FileServlet init param 'basePath' value '"
                        + this.basePath + "' is actually not a directory in file system.");
            } else if (!path.canRead()) {
                throw new ServletException("FileServlet init param 'basePath' value '"
                        + this.basePath + "' is actually not readable in file system.");
            }
        }
    }

    //Helpers (can be refactored to public utility class) ----------------------------------------
    /**
     * Returns true if the given accept header accepts the given value.
     *
     * @param acceptHeader The accept header.
     * @param toAccept The value to be accepted.
     * @return True if the given accept header accepts the given value.
     */
    private static boolean accepts(String acceptHeader, String toAccept) {
        String[] acceptValues = acceptHeader.split("\\s*(,|;)\\s*");
        Arrays.sort(acceptValues);
        return Arrays.binarySearch(acceptValues, toAccept) > -1
                || Arrays.binarySearch(acceptValues, toAccept.replaceAll("/.*$", "/*")) > -1
                || Arrays.binarySearch(acceptValues, "*/*") > -1;
    }

    /**
     * Returns true if the given match header matches the given value.
     *
     * @param matchHeader The match header.
     * @param toMatch The value to be matched.
     * @return True if the given match header matches the given value.
     */
    private static boolean matches(String matchHeader, String toMatch) {
        String[] matchValues = matchHeader.split("\\s*,\\s*");
        Arrays.sort(matchValues);
        return Arrays.binarySearch(matchValues, toMatch) > -1
                || Arrays.binarySearch(matchValues, "*") > -1;
    }

    /**
     * Returns a substring of the given string value from the given begin index
     * to the given end index as a long. If the substring is empty, then -1 will
     * be returned
     *
     * @param value The string value to return a substring as long for.
     * @param beginIndex The begin index of the substring to be returned as
     * long.
     * @param endIndex The end index of the substring to be returned as long.
     * @return A substring of the given string value as long or -1 if substring
     * is empty.
     */
    private static long sublong(String value, int beginIndex, int endIndex) {
        String substring = value.substring(beginIndex, endIndex);
        return (substring.length() > 0) ? Long.parseLong(substring) : -1;
    }

    /**
     * Copy the given byte range of the given input to the given output.
     *
     * @param input The input to copy the given range to the given output for.
     * @param output The output to copy the given range from the given input
     * for.
     * @param start Start of the byte range.
     * @param length Length of the byte range.
     * @throws IOException If something fails at I/O level.
     */
    private static void copy(RandomAccessFile input, OutputStream output, long start, long length)
            throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int read;

        if (input.length() == length) {
            // Write full range.
            while ((read = input.read(buffer)) > 0) {
                output.write(buffer, 0, read);
            }
        } else {
            // Write partial range.
            input.seek(start);
            long toRead = length;

            while ((read = input.read(buffer)) > 0) {
                if ((toRead -= read) > 0) {
                    output.write(buffer, 0, read);
                } else {
                    output.write(buffer, 0, (int) toRead + read);
                    break;
                }
            }
        }
    }

    /**
     * Close the given resource.
     *
     * @param resource The resource to be closed.
     */
    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException ignore) {
                // Ignore IOException. If you want to handle this anyway, it might be useful to know
                // that this will generally only be thrown when the client aborted the request.
            }
        }
    }

    //Inner classes ------------------------------------------------------------------------------
    /**
     * This class represents a byte range.
     */
    protected class Range {

        long start;
        long end;
        long length;
        long total;

        /**
         * Construct a byte range.
         *
         * @param start Start of the byte range.
         * @param end End of the byte range.
         * @param total Total length of the byte source.
         */
        public Range(long start, long end, long total) {
            this.start = start;
            this.end = end;
            this.length = end - start + 1;
            this.total = total;
        }
    }

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @param content
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, boolean content)
            throws ServletException, IOException {

        String requestedFile = request.getPathInfo();

        String absfilepath = (basePath + requestedFile).replace("//", "/");

        File file = new File(absfilepath);

        absfilepath = file.getAbsolutePath().replace('\\', '/');

        //PREVENTING PATH TRAVERSE
        if (absfilepath.startsWith(basePath) == false) {
            throw new AccessDeny("TRYING TO ACCESS PRIVATE FILE");
        }

        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            throw new RuntimeException("RESOURCE DOESN'T EXIST");
        }

        String fileName = file.getName();
        long length = file.length();
        long lastModified = file.lastModified();
        String eTag = fileName + "_" + length + "_" + lastModified;
        long expires = System.currentTimeMillis() + DEFAULT_EXPIRE_TIME;

        // Validate request headers for caching 
        // If-None-Match header should contain "*" or ETag. If so, then return 304.
        String ifNoneMatch = request.getHeader("If-None-Match");
        if (ifNoneMatch != null && matches(ifNoneMatch, eTag)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            response.setHeader("ETag", eTag); // Required in 304.
            response.setDateHeader("Expires", expires); // Postpone cache with 1 week.
            return;
        }

        // If-Modified-Since header should be greater than LastModified. If so, then return 304.
        // This header is ignored if any If-None-Match header is specified.
        long ifModifiedSince = request.getDateHeader("If-Modified-Since");
        if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince + 1000 > lastModified) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            response.setHeader("ETag", eTag); // Required in 304.
            response.setDateHeader("Expires", expires); // Postpone cache with 1 week.
            return;
        }

        // Validate request headers for resume
        // If-Match header should contain "*" or ETag. If not, then return 412.
        String ifMatch = request.getHeader("If-Match");
        if (ifMatch != null && !matches(ifMatch, eTag)) {
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            throw new RuntimeException("PRECONDITION FAILED");
        }

        // If-Unmodified-Since header should be greater than LastModified. If not, then return 412.
        long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
        if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= lastModified) {
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            throw new RuntimeException("PRECONDITION FAILED");
        }

        // Validate and process range 
        // Prepare some variables. The full Range represents the complete file.
        Range full = new Range(0, length - 1, length);
        List<Range> ranges = new ArrayList<Range>();

        // Validate and process Range and If-Range headers.
        String range = request.getHeader("Range");
        if (range != null) {

            // Range header should match format "bytes=n-n,n-n,n-n...". If not, then return 416.
            if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
                response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
                response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                throw new RuntimeException("Range header should match format \"bytes=n-n,n-n,n-n...\".");
            }

            // If-Range header should either match ETag or be greater then LastModified. If not,
            // then return full file.
            String ifRange = request.getHeader("If-Range");
            if (ifRange != null && !ifRange.equals(eTag)) {
                try {
                    long ifRangeTime = request.getDateHeader("If-Range"); // Throws IAE if invalid.
                    if (ifRangeTime != -1 && ifRangeTime + 1000 < lastModified) {
                        ranges.add(full);
                    }
                } catch (IllegalArgumentException ignore) {
                    ranges.add(full);
                }
            }

            // If any valid If-Range header, then process each part of byte range.
            if (ranges.isEmpty()) {
                for (String part : range.substring(6).split(",")) {
                    // Assuming a file with length of 100, the following examples returns bytes at:
                    // 50-80 (50 to 80), 40- (40 to length=100), -20 (length-20=80 to length=100).
                    long start = sublong(part, 0, part.indexOf("-"));
                    long end = sublong(part, part.indexOf("-") + 1, part.length());

                    if (start == -1) {
                        start = length - end;
                        end = length - 1;
                    } else if (end == -1 || end > length - 1) {
                        end = length - 1;
                    }

                    // Check if Range is syntactically valid. If not, then return 416.
                    if (start > end) {
                        response.setHeader("Content-Range", "bytes */" + length); // Required in 416.

                        response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                        throw new RuntimeException();
                    }

                    // Add range.
                    ranges.add(new Range(start, end, length));
                }
            }
        }

        // Prepare and initialize response 
        // Get content type by file name and set default GZIP support and content disposition.
        String contentType = getServletContext().getMimeType(fileName);
        boolean acceptsGzip = false;
        String disposition = "inline";

        // If content type is unknown, then set the default value.
        // For all content types, see: http://www.w3schools.com/media/media_mimeref.asp
        // To add new content types, add new mime-mapping entry in web.xml.
        if (contentType == null) {
            contentType = getContentTypeByFileName(absfilepath);

            //contentType = "application/octet-stream";
        }

        // If content type is text, then determine whether GZIP content encoding is supported by
        // the browser and expand content type with the one and right character encoding.
        if (contentType.startsWith("text")) {
            String acceptEncoding = request.getHeader("Accept-Encoding");
            acceptsGzip = acceptEncoding != null && accepts(acceptEncoding, "gzip");
            contentType += ";charset=UTF-8";
        } // Else, expect for images, determine content disposition. If content type is supported by
        // the browser, then set to inline, else attachment which will pop a 'save as' dialogue.
        else if (!contentType.startsWith("image")) {
            String accept = request.getHeader("Accept");
            disposition = accept != null && accepts(accept, contentType) ? "inline" : "attachment";
        }

        // Initialize response.
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setHeader("Content-Disposition", disposition + ";filename=\"" + fileName + "\"");
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("ETag", eTag);
        response.setDateHeader("Last-Modified", lastModified);
        response.setDateHeader("Expires", expires);

        // Send requested file (part(s)) to client ------------------------------------------------
        // Prepare streams.
        RandomAccessFile input = null;
        OutputStream output = null;

        try {
            // Open streams.
            input = new RandomAccessFile(file, "r");
            output = response.getOutputStream();

            if (ranges.isEmpty() || ranges.get(0) == full) {

                // Return full file.
                Range r = full;
                response.setContentType(contentType);
                response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);

                if (content) {
                    if (acceptsGzip) {
                        // The browser accepts GZIP, so GZIP the content.
                        response.setHeader("Content-Encoding", "gzip");
                        output = new GZIPOutputStream(output, DEFAULT_BUFFER_SIZE);
                    } else {
                        // Content length is not directly predictable in case of GZIP.
                        // So only add it if there is no means of GZIP, else browser will hang.
                        response.setHeader("Content-Length", String.valueOf(r.length));
                    }

                    // Copy full range.
                    copy(input, output, r.start, r.length);
                }

            } else if (ranges.size() == 1) {

                // Return single part of file.
                Range r = ranges.get(0);
                response.setContentType(contentType);
                response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);
                response.setHeader("Content-Length", String.valueOf(r.length));
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

                if (content) {
                    // Copy single part range.
                    copy(input, output, r.start, r.length);
                }

            } else {

                // Return multiple parts of file.
                response.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

                if (content) {
                    // Cast back to ServletOutputStream to get the easy println methods.
                    ServletOutputStream sos = (ServletOutputStream) output;

                    // Copy multi part range.
                    for (Range r : ranges) {
                        // Add multipart boundary and header fields for every range.
                        sos.println();
                        sos.println("--" + MULTIPART_BOUNDARY);
                        sos.println("Content-Type: " + contentType);
                        sos.println("Content-Range: bytes " + r.start + "-" + r.end + "/" + r.total);

                        // Copy single part range of multi part range.
                        copy(input, output, r.start, r.length);
                    }

                    // End with multipart boundary.
                    sos.println();
                    sos.println("--" + MULTIPART_BOUNDARY + "--");
                }
            }
        } finally {
            // Gently close streams.
            close(output);
            close(input);
        }
    }

    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response, false);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response, true);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    private static final Map<String, String> fileExtensionMap;

    static {
        fileExtensionMap = new HashMap<String, String>();
        fileExtensionMap.put("doc", "application/msword");
        fileExtensionMap.put("dot", "application/msword");
        fileExtensionMap.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        //fileExtensionMap.put("js", "application/javascript");
        //fileExtensionMap.put("html", "text/html");
        fileExtensionMap.put("ts", "text/javascript");
        //fileExtensionMap.put("gif", "image/gif");
        //fileExtensionMap.put("bmp", "image/bmp");
        //fileExtensionMap.put("jpeg", "image/jpeg");
        //fileExtensionMap.put("pjepg", "image/pjpeg");
        //fileExtensionMap.put("png", "image/png");
        //fileExtensionMap.put("svg", "image/svg+xml");
    }

    public static String getContentTypeByFileName(String fileName) {
        // 1. first use java's buildin utils
        FileNameMap mimeTypes = URLConnection.getFileNameMap();
        String contentType = mimeTypes.getContentTypeFor(fileName);
        // 2. nothing found -> lookup our in extension map to find types like ".doc" or ".docx"
        if (contentType == null || "".equals(contentType)) {
            String extension = FilenameUtils.getExtension(fileName);
            contentType = fileExtensionMap.get(extension);
            if (contentType == null || "".equals(contentType)) {
                contentType = "application/octet-stream";
            }
        }
        return contentType;
    }
}
