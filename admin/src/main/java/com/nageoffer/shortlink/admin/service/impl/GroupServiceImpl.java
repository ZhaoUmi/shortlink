package com.nageoffer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.admin.common.biz.user.UserContext;
import com.nageoffer.shortlink.admin.dao.entity.GroupDO;
import com.nageoffer.shortlink.admin.dao.mapper.GroupMapper;
import com.nageoffer.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.nageoffer.shortlink.admin.service.GroupService;
import com.nageoffer.shortlink.admin.toolkit.RandomGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 短链接分组接口实现层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

//    private final RBloomFilter<String> gidRegisterCachePenetrationBloomFilter;
//    private final GroupUniqueMapper groupUniqueMapper;
//    private final ShortLinkActualRemoteService shortLinkActualRemoteService;
//    private final RedissonClient redissonClient;
//
//    @Value("${short-link.group.max-num}")
//    private Integer groupMaxNum;
//
    @Override
    public void saveGroup(String groupName) {
        String gid;
        do{
            gid= RandomGenerator.generateRandom();
        }while (!hasGid(gid));
        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .sortOrder(0)
                .username(UserContext.getUsername())
                .name(groupName)
                .build();

        baseMapper.insert(groupDO);
    }

    private boolean hasGid(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername,UserContext.getUsername());
        GroupDO hasGroupFlag = baseMapper.selectOne(queryWrapper);
        return hasGroupFlag == null;
    }
//    @Override
//    public void saveGroup(String groupName) {
//        saveGroup(UserContext.getUsername(), groupName);
//    }
//
//    @Override
//    public void saveGroup(String username, String groupName) {
//        RLock lock = redissonClient.getLock(String.format(LOCK_GROUP_CREATE_KEY, username));
//        lock.lock();
//        try {
//            LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
//                    .eq(GroupDO::getUsername, username)
//                    .eq(GroupDO::getDelFlag, 0);
//            List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
//            if (CollUtil.isNotEmpty(groupDOList) && groupDOList.size() == groupMaxNum) {
//                throw new ClientException(String.format("已超出最大分组数：%d", groupMaxNum));
//            }
//            int retryCount = 0;
//            int maxRetries = 10;
//            String gid = null;
//            while (retryCount < maxRetries) {
//                gid = saveGroupUniqueReturnGid();
//                if (StrUtil.isNotEmpty(gid)) {
//                    GroupDO groupDO = GroupDO.builder()
//                            .gid(gid)
//                            .sortOrder(0)
//                            .username(username)
//                            .name(groupName)
//                            .build();
//                    baseMapper.insert(groupDO);
//                    gidRegisterCachePenetrationBloomFilter.add(gid);
//                    break;
//                }
//                retryCount++;
//            }
//            if (StrUtil.isEmpty(gid)) {
//                throw new ServiceException("生成分组标识频繁");
//            }
//        } finally {
//            lock.unlock();
//        }
//    }
//
    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
//        Result<List<ShortLinkGroupCountQueryRespDTO>> listResult = shortLinkActualRemoteService
//                .listGroupShortLinkCount(groupDOList.stream().map(GroupDO::getGid).toList());
//        List<ShortLinkGroupRespDTO> shortLinkGroupRespDTOList = BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
//        shortLinkGroupRespDTOList.forEach(each -> {
//            Optional<ShortLinkGroupCountQueryRespDTO> first = listResult.getData().stream()
//                    .filter(item -> Objects.equals(item.getGid(), each.getGid()))
//                    .findFirst();
//            first.ifPresent(item -> each.setShortLinkCount(first.get().getShortLinkCount()));
//        });
//        return shortLinkGroupRespDTOList;
//    }
        return BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
    }
//
//    @Override
//    public void updateGroup(ShortLinkGroupUpdateReqDTO requestParam) {
//        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
//                .eq(GroupDO::getUsername, UserContext.getUsername())
//                .eq(GroupDO::getGid, requestParam.getGid())
//                .eq(GroupDO::getDelFlag, 0);
//        GroupDO groupDO = new GroupDO();
//        groupDO.setName(requestParam.getName());
//        baseMapper.update(groupDO, updateWrapper);
//    }
//
//    @Override
//    public void deleteGroup(String gid) {
//        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
//                .eq(GroupDO::getUsername, UserContext.getUsername())
//                .eq(GroupDO::getGid, gid)
//                .eq(GroupDO::getDelFlag, 0);
//        GroupDO groupDO = new GroupDO();
//        groupDO.setDelFlag(1);
//        baseMapper.update(groupDO, updateWrapper);
//    }
//
//    @Override
//    public void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam) {
//        requestParam.forEach(each -> {
//            GroupDO groupDO = GroupDO.builder()
//                    .sortOrder(each.getSortOrder())
//                    .build();
//            LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
//                    .eq(GroupDO::getUsername, UserContext.getUsername())
//                    .eq(GroupDO::getGid, each.getGid())
//                    .eq(GroupDO::getDelFlag, 0);
//            baseMapper.update(groupDO, updateWrapper);
//        });
//    }
//
//    private String saveGroupUniqueReturnGid() {
//        String gid = RandomGenerator.generateRandom();
//        if (!gidRegisterCachePenetrationBloomFilter.contains(gid)) {
//            GroupUniqueDO groupUniqueDO = GroupUniqueDO.builder()
//                    .gid(gid)
//                    .build();
//            try {
//                groupUniqueMapper.insert(groupUniqueDO);
//            } catch (DuplicateKeyException e) {
//                return null;
//            }
//        }
//        return gid;
//    }
}
