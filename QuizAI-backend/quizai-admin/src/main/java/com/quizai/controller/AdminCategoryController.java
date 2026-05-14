package com.quizai.controller;

import com.quizai.domain.Category;
import com.quizai.domain.R;
import com.quizai.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/category")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("list")
    public R<List<Category>> list() {
        List<Category> list = categoryService.list();
        return R.success(list);
    }

    @PostMapping("add")
    public R add(@RequestBody Category category) {
        boolean flag = categoryService.save(category);
        return flag ? R.success("添加成功") : R.error("添加失败");
    }

    @PostMapping("update")
    public R update(@RequestBody Category category) {
        boolean flag = categoryService.updateById(category);
        return flag ? R.success("更新成功") : R.error("更新失败");
    }

    @PostMapping("delete")
    public R delete(@RequestBody Category category) {
        boolean flag = categoryService.removeById(category.getId());
        return flag ? R.success("删除成功") : R.error("删除失败");
    }
}
