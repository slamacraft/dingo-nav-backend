package com.dingo.module.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.dingo.module.entity.PropsEntity
import com.dingo.module.mapper.PropsMapper
import com.dingo.module.service.IPropsService
import org.springframework.stereotype.Service

@Service("PropsService")
open class PropsServiceImpl : ServiceImpl<PropsMapper, PropsEntity>(), IPropsService {
}
