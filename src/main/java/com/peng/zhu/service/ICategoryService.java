package com.peng.zhu.service;

import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.pojo.Category;

import java.util.List;

public interface ICategoryService {
    public ServerResponse<String> addCategroy(String categroyName, Integer parentId);
    public ServerResponse<String> setCategroyName(String categroyName,Integer categroyId);
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer parentId);
    public ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory(Integer categoryId);
}
