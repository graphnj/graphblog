package com.zjh.project.graphblog;

import com.zjh.project.graphblog.constant.Const;
import com.zjh.project.graphblog.modal.Vo.OptionVo;
import com.zjh.project.graphblog.service.IOptionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by aaabes on 2017/6/13.
 */
@Component
public class ConfigLoader implements CommandLineRunner{

    @Resource
    IOptionService optionService;

    @Override
    public void run(String... args) throws Exception {
        if (Const.initConfig.size() ==0) {
            List<OptionVo> voList = optionService.getOptions();

            voList.forEach((option) -> {
                Const.initConfig.put(option.getName(), option.getValue());
            });

        }
    }
}
