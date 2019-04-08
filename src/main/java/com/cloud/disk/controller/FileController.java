package com.cloud.disk.controller;

import com.baidu.aip.imageclassify.AipImageClassify;
import com.cloud.disk.core.ResponseBean;
import com.cloud.disk.core.UnicomResponseEnums;
import com.cloud.disk.domain.AdvancedGeneral;
import com.cloud.disk.domain.FileSaveInfo;
import com.cloud.disk.domain.Node;
import com.cloud.disk.domain.User;
import com.cloud.disk.dtos.EditUserDto;
import com.cloud.disk.repository.AdvancedGeneralJpaResitory;
import com.cloud.disk.repository.FileJpaRepository;
import com.cloud.disk.repository.NodeJpaRepository;
import com.cloud.disk.repository.UserJpaRepository;
import com.cloud.disk.service.FolderService;
import com.cloud.disk.units.FileTypeUtil;
import com.cloud.disk.units.UnitHelper;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/file")
public class FileController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    @Autowired
    private FileJpaRepository fileJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private NodeJpaRepository nodeJpaRepository;

    @Autowired
    private FolderService folderService;

    @Value("${file.save.path}")
    private String filePath;

    private static Logger log = LoggerFactory.getLogger(FileController.class);
    @Value("${APP_ID}")
    private  String APP_ID;
    @Value("${API_KEY}")
    private   String API_KEY;
    @Value("${SECRET_KEY}")
    private   String SECRET_KEY;

    @Autowired
    private AdvancedGeneralJpaResitory advancedGeneralJpaResitory;

    @RequestMapping("/t")
    public ModelAndView t() {
        return new ModelAndView("/file/t");
    }

    @RequestMapping("/test")
    public ModelAndView test() {
        return new ModelAndView("/file/test");
    }

    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView modelAndView = UnitHelper.GetCurrentUserAndView();
        modelAndView.setViewName("/file/index");
//        List<Folder>folders= folderService.GetTreePosition();
//        modelAndView.addObject("folders",folders);
        return modelAndView;
    }

    //文件上传相关代码
    @RequestMapping(value = "upload")
    public ResponseBean<UnicomResponseEnums> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseBean.error("文件为空!");
        }
        // 获取文件名
        String fileName = file.getOriginalFilename();
        logger.info("上传的文件名为：" + fileName);
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        logger.info("上传的后缀名为：" + suffixName);
        // 文件上传后的路径
        // 解决中文问题，liunx下中文路径，图片显示问题
        fileName = UUID.randomUUID() + suffixName;
        File dest = new File(filePath + "merge/" + fileName);
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
            return ResponseBean.success(fileName, "上传成功!");
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseBean.error("上传失败!");
    }

    //多文件上传
    @RequestMapping(value = "/batch/upload", method = RequestMethod.POST)
    @ResponseBody
    public String handleFileUpload(HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        MultipartFile file = null;
        BufferedOutputStream stream = null;
        for (int i = 0; i < files.size(); ++i) {
            file = files.get(i);
            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    stream = new BufferedOutputStream(new FileOutputStream(new File(file.getOriginalFilename())));
                    stream.write(bytes);
                    stream.close();

                } catch (Exception e) {
                    stream = null;
                    return "You failed to upload " + i + " => " + e.getMessage();
                }
            } else {
                return "You failed to upload " + i + " because the file was empty.";
            }
        }
        return "upload successful";
    }

    //当前登录人头像
    @RequestMapping("/getCurrentProfile")
    public void getCurrentProfile(HttpServletRequest request, HttpServletResponse response) {
        EditUserDto sessionUser = (EditUserDto) SecurityUtils.getSubject().getPrincipal();
        List<User> currentUser = userJpaRepository.findByLoginName(sessionUser.getLoginName());
        User ccUser = currentUser.size() > 0 ? currentUser.get(0) : null;
        String fileName = ccUser.getPicture();
        if (fileName == null) {
            fileName = "avatar5.png";
        }
        this.downloadFile(request, response, fileName, fileName);
    }


    //文件下载相关代码
    @RequestMapping("/download")
    public ResponseBean<UnicomResponseEnums> downloadFile(HttpServletRequest request, HttpServletResponse
            response, @RequestParam(value = "fileToken") String fileToken, @RequestParam(value = "newName") String newName) {
        if (fileToken != null) {
            //当前是从该工程的WEB-INF//File//下获取文件(该目录可以在下面一行代码配置)然后下载到C:\\users\\downloads即本机的默认下载的目录
            // String realPath = request.getServletContext().getRealPath("//WEB-INF//UploadFile//");
            String realPath = filePath + "merge";
            File file = new File(realPath, fileToken);
            if (file.exists()) {
                response.setContentType("application/force-download");// 设置强制下载不打开
                response.addHeader("Content-Disposition", "attachment;fileName=" + newName);// 设置文件名
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    System.out.println("success");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return ResponseBean.error("文件为空");
    }



    @RequestMapping(value = "/md5validate", method = RequestMethod.POST)
    public ResponseBean<UnicomResponseEnums> Md5Validate(@RequestBody Node node) {

        System.out.println("进入md5validate方法");
        String fileToken = UUID.randomUUID() + "." + node.getFileName().substring(node.getFileName().lastIndexOf(".") + 1);
        System.out.println("文件token:"+fileToken);

        List<Node> nodes = nodeJpaRepository.findAllByMd5Val(node.getMd5Val());
        if (nodes.size() == 0) {
            System.out.println("存在相同的文件");
            return ResponseBean.error("非秒传");
        }
//        Long currentUserId =  ((EditUserDto) SecurityUtils.getSubject().getPrincipal()).getId();
//        Node newNode=new Node(fileToken, node.getFileName(),node.getFileToken(),currentUserId);
//        nodeJpaRepository.save(newNode);
        return ResponseBean.success(nodes.get(0).getFileToken());
    }

    @RequestMapping(value = "/delete/{fileToken}", method = RequestMethod.DELETE)
    public ResponseBean<UnicomResponseEnums> delete(String fileToken) {
        List<FileSaveInfo> files = fileJpaRepository.findByfileToken(fileToken);
        Long currentUserId = ((EditUserDto) SecurityUtils.getSubject().getPrincipal()).getId();
        if (currentUserId == files.get(0).getAdduserid()) {
            fileJpaRepository.delete(files.get(0));
        } else {
            return ResponseBean.error("无权删除别人上传的文件!");
        }
        return ResponseBean.success();
    }


    /**
     * 上传文件
     *
     * @param request
     * @param response
     * @param guid
     * @param chunk
     * @param file
     * @param chunks
     */
    @RequestMapping("/bigFile")
    public void bigFile(HttpServletRequest request, HttpServletResponse response, String guid, Integer
            chunk, MultipartFile file, Integer chunks) {
        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (chunk == null) chunk = 0;
            if (isMultipart) {
                // 临时目录用来存放所有分片文件
                String tempFileDir = filePath + guid;
                File parentFileDir = new File(tempFileDir);
                if (!parentFileDir.exists()) {
                    parentFileDir.mkdirs();
                }
                // 分片处理时，前台会多次调用上传接口，每次都会上传文件的一部分到后台
                File tempPartFile = new File(parentFileDir, guid + "_" + chunk + ".part");
                FileUtils.copyInputStreamToFile(file.getInputStream(), tempPartFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 合并文件
     *
     * @param guid
     * @param fileName
     * @throws Exception
     */
    @RequestMapping("/merge")
    @ResponseBody
    public ResponseBean<UnicomResponseEnums> mergeFile(String guid, String fileName, String md5Val, String fileSize) {
        fileName=fileName.replace(" ","");
        // 得到 destTempFile 就是最终的文件
        String ext = fileName.substring(fileName.lastIndexOf("."), fileName.length());
        try {
            File parentFileDir = new File(filePath + guid);
            if (parentFileDir.isDirectory()) {
                File destTempFile = new File(filePath + "merge", guid + ext);
                if (!destTempFile.exists()) {
                    //先得到文件的上级目录，并创建上级目录，在创建文件,
                    destTempFile.getParentFile().mkdir();
                    try {
                        //创建文件
                        destTempFile.createNewFile(); //上级目录没有创建，这里会报错
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(parentFileDir.listFiles().length);
                for (int i = 0; i < parentFileDir.listFiles().length; i++) {
                    File partFile = new File(parentFileDir, guid + "_" + i + ".part");
                    FileOutputStream destTempfos = new FileOutputStream(destTempFile, true);
                    //遍历"所有分片文件"到"最终文件"中
                    FileUtils.copyFile(partFile, destTempfos);
                    destTempfos.close();
                }
                String contentType = null;

                try {
                    contentType = Files.probeContentType(Paths.get(filePath + "merge/" + guid + ext));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // 删除临时目录中的分片文件
                FileUtils.deleteDirectory(parentFileDir);
                Long currentUserId = ((EditUserDto) SecurityUtils.getSubject().getPrincipal()).getId();
                int fileType = FileTypeUtil.getType(fileName);
                Node newNode = new Node(md5Val, fileName, guid + ext, currentUserId, fileType);
                newNode.setExt(ext);
                newNode.setContentType(contentType);
                newNode.setFileSize(Long.parseLong(fileSize));
                nodeJpaRepository.saveAndFlush(newNode);

                if(fileType==1){
                    AipImageClassify client = new com.baidu.aip.imageclassify.AipImageClassify(APP_ID, API_KEY, SECRET_KEY);
                    client.setConnectionTimeoutInMillis(2000);
                    client.setSocketTimeoutInMillis(60000);
                    String path = filePath + "merge/" + guid + ext;
                    org.json.JSONObject res = client.advancedGeneral(path, new HashMap<String, String>());
                    JSONArray jsonValues= res.getJSONArray("result");
                    for (int i=0;i<jsonValues.length();++i) {
                        JSONObject h=(JSONObject) jsonValues.get(i);
                        h.getDouble("score");
                        AdvancedGeneral ad=  new AdvancedGeneral(h.getDouble("score"),h.getString("root"),h.getString("keyword"));
                        ad.setNodeId(newNode.getId());
                        advancedGeneralJpaResitory.save(ad);
                    }
                }


                return ResponseBean.success(guid + ext);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBean.error(e.getMessage());
        }
        return ResponseBean.success(guid + ext);
    }

    @RequestMapping(value="/test1")
    public void test1()
    {
        AipImageClassify client = new com.baidu.aip.imageclassify.AipImageClassify(APP_ID, API_KEY, SECRET_KEY);
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        String path = filePath + "merge/wu_1d5hf6rmq8fq1h0r15ll7jt1rjt1v.jpg";
        org.json.JSONObject res = client.advancedGeneral(path, new HashMap<String, String>());

        JSONArray jsonValues= res.getJSONArray("result");
        for (int i=0;i<jsonValues.length();++i) {
              JSONObject h=(JSONObject) jsonValues.get(i);
               h.getDouble("score");
               AdvancedGeneral ad=  new AdvancedGeneral(h.getDouble("score"),h.getString("root"),h.getString("keyword"));

       }
    }

}
