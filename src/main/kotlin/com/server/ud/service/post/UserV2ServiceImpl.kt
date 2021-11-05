package com.server.ud.service.post

import com.server.common.provider.AuthProvider
import com.server.ud.provider.post.PostProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserV2ServiceImpl : UserV2Service() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var postProvider: PostProvider
}
