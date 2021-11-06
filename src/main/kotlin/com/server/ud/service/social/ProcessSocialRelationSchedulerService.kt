package com.server.ud.service.social

import com.server.ud.entities.social.SocialRelation

abstract class ProcessSocialRelationSchedulerService {
    abstract fun createSocialRelationProcessingJob(socialRelation: SocialRelation): SocialRelation
}
