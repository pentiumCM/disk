package com.cloud.disk.service;

import com.cloud.disk.domain.FileSaveInfo;
import com.cloud.disk.domain.Recycle;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface NodeSerice {
     List<FileSaveInfo> GetFileGrid(String search, int folderid);
    void backfile(Recycle recycle);
}
