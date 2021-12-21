package com.dingdo.module.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.dingdo.module.entity.PropsEntity
import com.dingdo.module.mapper.PropsMapper
import com.dingdo.module.service.IPropsService
import org.springframework.stereotype.Service

@Service("PropsService")
open class PropsServiceImpl : ServiceImpl<PropsMapper, PropsEntity>(), IPropsService {
}
