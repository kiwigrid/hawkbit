/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.rest.resource;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.hawkbit.repository.DistributionSetManagement;
import org.eclipse.hawkbit.repository.TagFields;
import org.eclipse.hawkbit.repository.TagManagement;
import org.eclipse.hawkbit.repository.exception.EntityNotFoundException;
import org.eclipse.hawkbit.repository.model.DistributionSet;
import org.eclipse.hawkbit.repository.model.DistributionSetTag;
import org.eclipse.hawkbit.repository.model.DistributionSetTagAssigmentResult;
import org.eclipse.hawkbit.repository.rsql.RSQLUtility;
import org.eclipse.hawkbit.rest.resource.api.DistributionSetTagRestApi;
import org.eclipse.hawkbit.rest.resource.model.distributionset.DistributionSetsRest;
import org.eclipse.hawkbit.rest.resource.model.tag.AssignedDistributionSetRequestBody;
import org.eclipse.hawkbit.rest.resource.model.tag.DistributionSetTagAssigmentResultRest;
import org.eclipse.hawkbit.rest.resource.model.tag.TagPagedList;
import org.eclipse.hawkbit.rest.resource.model.tag.TagRequestBodyPut;
import org.eclipse.hawkbit.rest.resource.model.tag.TagRest;
import org.eclipse.hawkbit.rest.resource.model.tag.TagsRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Resource handling for {@link DistributionSetTag} CRUD operations.
 *
 */
@RestController
public class DistributionSetTagResource implements DistributionSetTagRestApi {
    private static final Logger LOG = LoggerFactory.getLogger(DistributionSetTagResource.class);

    @Autowired
    private TagManagement tagManagement;

    @Autowired
    private DistributionSetManagement distributionSetManagement;

    @Override
    public ResponseEntity<TagPagedList> getDistributionSetTags(final int pagingOffsetParam, final int pagingLimitParam,
            final String sortParam, final String rsqlParam) {

        final int sanitizedOffsetParam = PagingUtility.sanitizeOffsetParam(pagingOffsetParam);
        final int sanitizedLimitParam = PagingUtility.sanitizePageLimitParam(pagingLimitParam);
        final Sort sorting = PagingUtility.sanitizeTargetSortParam(sortParam);

        final Pageable pageable = new OffsetBasedPageRequest(sanitizedOffsetParam, sanitizedLimitParam, sorting);
        final Slice<DistributionSetTag> findTargetsAll;
        final Long countTargetsAll;
        if (rsqlParam == null) {
            findTargetsAll = this.tagManagement.findAllDistributionSetTags(pageable);
            countTargetsAll = this.tagManagement.countTargetTags();

        } else {
            final Page<DistributionSetTag> findTargetPage = this.tagManagement
                    .findAllDistributionSetTags(RSQLUtility.parse(rsqlParam, TagFields.class), pageable);
            countTargetsAll = findTargetPage.getTotalElements();
            findTargetsAll = findTargetPage;

        }

        final List<TagRest> rest = TagMapper.toResponseDistributionSetTag(findTargetsAll.getContent());
        return new ResponseEntity<>(new TagPagedList(rest, countTargetsAll), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TagRest> getDistributionSetTag(final Long distributionsetTagId) {
        final DistributionSetTag tag = findDistributionTagById(distributionsetTagId);
        return new ResponseEntity<>(TagMapper.toResponse(tag), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TagsRest> createDistributionSetTags(final List<TagRequestBodyPut> tags) {
        LOG.debug("creating {} ds tags", tags.size());

        final List<DistributionSetTag> createdTags = this.tagManagement
                .createDistributionSetTags(TagMapper.mapDistributionSetTagFromRequest(tags));

        return new ResponseEntity<>(TagMapper.toResponseDistributionSetTag(createdTags), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<TagRest> updateDistributionSetTag(final Long distributionsetTagId,
            final TagRequestBodyPut restDSTagRest) {
        LOG.debug("update {} ds tag", restDSTagRest);

        final DistributionSetTag distributionSetTag = findDistributionTagById(distributionsetTagId);
        TagMapper.updateTag(restDSTagRest, distributionSetTag);
        final DistributionSetTag updateDistributionSetTag = this.tagManagement
                .updateDistributionSetTag(distributionSetTag);

        LOG.debug("ds tag updated");

        return new ResponseEntity<>(TagMapper.toResponse(updateDistributionSetTag), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteDistributionSetTag(final Long distributionsetTagId) {
        LOG.debug("Delete {} distribution set tag", distributionsetTagId);
        final DistributionSetTag tag = findDistributionTagById(distributionsetTagId);

        this.tagManagement.deleteDistributionSetTag(tag.getName());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DistributionSetsRest> getAssignedDistributionSets(final Long distributionsetTagId) {
        final DistributionSetTag tag = findDistributionTagById(distributionsetTagId);
        return new ResponseEntity<>(
                DistributionSetMapper.toResponseDistributionSets(tag.getAssignedToDistributionSet()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DistributionSetTagAssigmentResultRest> toggleTagAssignment(final Long distributionsetTagId,
            final List<AssignedDistributionSetRequestBody> assignedDSRequestBodies) {
        LOG.debug("Toggle distribution set assignment {} for ds tag {}", assignedDSRequestBodies.size(),
                distributionsetTagId);

        final DistributionSetTag tag = findDistributionTagById(distributionsetTagId);

        final DistributionSetTagAssigmentResult assigmentResult = this.distributionSetManagement
                .toggleTagAssignment(findDistributionSetIds(assignedDSRequestBodies), tag.getName());

        final DistributionSetTagAssigmentResultRest tagAssigmentResultRest = new DistributionSetTagAssigmentResultRest();
        tagAssigmentResultRest.setAssignedDistributionSets(
                DistributionSetMapper.toResponseDistributionSets(assigmentResult.getAssignedDs()));
        tagAssigmentResultRest.setUnassignedDistributionSets(
                DistributionSetMapper.toResponseDistributionSets(assigmentResult.getUnassignedDs()));

        LOG.debug("Toggled assignedDS {} and unassignedDS{}", assigmentResult.getAssigned(),
                assigmentResult.getUnassigned());

        return new ResponseEntity<>(tagAssigmentResultRest, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DistributionSetsRest> assignDistributionSets(final Long distributionsetTagId,
            final List<AssignedDistributionSetRequestBody> assignedDSRequestBodies) {
        LOG.debug("Assign DistributionSet {} for ds tag {}", assignedDSRequestBodies.size(), distributionsetTagId);
        final DistributionSetTag tag = findDistributionTagById(distributionsetTagId);

        final List<DistributionSet> assignedDs = this.distributionSetManagement
                .assignTag(findDistributionSetIds(assignedDSRequestBodies), tag);
        LOG.debug("Assignd DistributionSet {}", assignedDs.size());
        return new ResponseEntity<>(DistributionSetMapper.toResponseDistributionSets(assignedDs), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> unassignDistributionSets(final Long distributionsetTagId) {
        LOG.debug("Unassign all DS for ds tag {}", distributionsetTagId);
        final DistributionSetTag tag = findDistributionTagById(distributionsetTagId);
        if (tag.getAssignedToDistributionSet() == null) {
            LOG.debug("No assigned ds founded");
            return new ResponseEntity<>(HttpStatus.OK);
        }

        final List<DistributionSet> distributionSets = this.distributionSetManagement
                .unAssignAllDistributionSetsByTag(tag);
        LOG.debug("Unassigned ds {}", distributionSets.size());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> unassignDistributionSet(final Long distributionsetTagId, final Long distributionsetId) {
        LOG.debug("Unassign ds {} for ds tag {}", distributionsetId, distributionsetTagId);
        final DistributionSetTag tag = findDistributionTagById(distributionsetTagId);
        this.distributionSetManagement.unAssignTag(distributionsetId, tag);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private DistributionSetTag findDistributionTagById(final Long distributionsetTagId) {
        final DistributionSetTag tag = this.tagManagement.findDistributionSetTagById(distributionsetTagId);
        if (tag == null) {
            throw new EntityNotFoundException("Distribution Tag with Id {" + distributionsetTagId + "} does not exist");
        }
        return tag;
    }

    private List<Long> findDistributionSetIds(
            final List<AssignedDistributionSetRequestBody> assignedDistributionSetRequestBodies) {
        return assignedDistributionSetRequestBodies.stream().map(request -> request.getDistributionSetId())
                .collect(Collectors.toList());
    }
}