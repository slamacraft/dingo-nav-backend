package com.dingo.module.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.dingo.module.entity.PropsEntity
import org.apache.ibatis.annotations.Mapper

@Mapper
interface PropsMapper :BaseMapper<PropsEntity>{
}
