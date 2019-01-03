package com.peng.zhu.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.dao.CategoryMapper;
import com.peng.zhu.pojo.Category;
import com.peng.zhu.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Set;
@Service("iCategoryService")
public class CategoryServiceImpl  implements ICategoryService {
    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse<String> addCategroy(String categroyName,Integer parentId){
        if(parentId == null && StringUtils.isBlank(categroyName)){
            return ServerResponse.createByErrorMessage("添加商品参数错误，不能添加!");
        }
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categroyName);
        int rowCount = categoryMapper.insert(category);
        if(rowCount>0){
            return ServerResponse.createBySuccessMessage("商品种类添加成功!");
        }
        return ServerResponse.createByErrorMessage("商品种类添加失败!");
    }

    public ServerResponse<String> setCategroyName(String categroyName,Integer categroyId){
        if(categroyId==null && StringUtils.isBlank(categroyName)){
            return ServerResponse.createByErrorMessage("商品添加参数为空，不能添加!");
        }
        Category category = new Category();
        category.setId(categroyId);
        category.setName(categroyName);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount>0){
            return  ServerResponse.createBySuccessMessage("商品添加成功!");
        }
        return ServerResponse.createByError();
    }
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer parentId){
        List<Category> categoryList = categoryMapper.selectChildrenParallelCategory(parentId);
        if(CollectionUtils.isEmpty(categoryList)){
            logger.error("未找到分类的子分类!");
        }
        return ServerResponse.createBySuccess(categoryList);
    }
    public ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findCategoryAndChildrenCategory(categorySet,categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId!=null){
            for(Category item:categorySet){
                categoryIdList.add(item.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }
    //递归算法
    private Set<Category> findCategoryAndChildrenCategory(Set<Category> setCategory,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category!=null){
            setCategory.add(category);
        }
        List<Category> categoryList = categoryMapper.selectChildrenParallelCategory(categoryId);
        for(Category categoryItem:categoryList){
            findCategoryAndChildrenCategory(setCategory,categoryItem.getId());
        }
        return setCategory;
    }
}
