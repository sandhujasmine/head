/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */

package org.mifos.platform.questionnaire.mappers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.customers.surveys.business.Question;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.framework.components.fieldConfiguration.business.EntityMaster;
import org.mifos.platform.questionnaire.contract.*;
import org.mifos.platform.questionnaire.domain.*;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mifos.platform.questionnaire.persistence.QuestionDao;
import org.mifos.test.matchers.EventSourceMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mifos.customers.surveys.helpers.AnswerType.FREETEXT;
import static org.mifos.platform.questionnaire.domain.QuestionGroupState.ACTIVE;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireMapperTest {
    private static final String TITLE = "Title";
    private QuestionnaireMapper questionnaireMapper;
    private static final String SECTION_NAME = "S1";
    private String SECTION = "section";

    @Mock
    private EventSourceDao eventSourceDao;

    @Mock
    private QuestionDao questionDao;

    @Before
    public void setUp() {
        questionnaireMapper = new QuestionnaireMapperImpl(eventSourceDao, questionDao);
    }

    @Test
    public void shouldMapQuestionDefinitionToQuestion() {
        QuestionDefinition questionDefinition = new QuestionDefinition(TITLE, QuestionType.FREETEXT);
        Question question = questionnaireMapper.mapToQuestion(questionDefinition);
        assertThat(question.getAnswerTypeAsEnum(), is(FREETEXT));
        assertThat(question.getQuestionText(), is(TITLE));
    }

    @Test
    public void shouldMapQuestionToQuestionDetail() {
        Question question = getQuestion(TITLE, AnswerType.FREETEXT);
        QuestionDetail questionDetail = questionnaireMapper.mapToQuestionDetail(question);
        assertQuestionDetail(questionDetail, TITLE, QuestionType.FREETEXT);
    }

    @Test
    public void shouldMapQuestionsToQuestionDetails() {
        int countOfQuestions = 10;
        List<Question> questions = new ArrayList<Question>();
        for (int i = 0; i < countOfQuestions; i++) {
            questions.add(getQuestion(TITLE + i, AnswerType.FREETEXT));
        }
        List<QuestionDetail> questionDetails = questionnaireMapper.mapToQuestionDetails(questions);
        for (int i = 0; i < countOfQuestions; i++) {
            assertQuestionDetail(questionDetails.get(i), TITLE + i, QuestionType.FREETEXT);
        }
    }

    @Test
    public void shouldMapQuestionDetailWithVariousAnswerTypes() {
        assertQuestionType(QuestionType.INVALID, AnswerType.INVALID);
        assertQuestionType(QuestionType.FREETEXT, AnswerType.FREETEXT);
        assertQuestionType(QuestionType.NUMERIC, AnswerType.NUMBER);
        assertQuestionType(QuestionType.DATE, AnswerType.DATE);
    }

    @Test
    public void shouldMapQuestionGroupDefinitionToQuestionGroup() {
        when(eventSourceDao.retrieveByEventAndSource(anyString(), anyString())).thenReturn(new ArrayList());
        when(questionDao.getDetails(12)).thenReturn(new Question());
        EventSource eventSource = getEventSource("Create", "Client");
        List<SectionDefinition> sectionDefinitions = asList(getSectionDefinition(SECTION_NAME));
        QuestionGroupDefinition questionGroupDefinition = new QuestionGroupDefinition(TITLE, eventSource, sectionDefinitions);
        QuestionGroup questionGroup = questionnaireMapper.mapToQuestionGroup(questionGroupDefinition);
        assertQuestionGroup(questionGroup);
        verify(eventSourceDao, times(1)).retrieveByEventAndSource(anyString(), anyString());
        verify(questionDao, times(1)).getDetails(12);
    }

    private void assertQuestionGroup(QuestionGroup questionGroup) {
        assertThat(questionGroup, notNullValue());
        assertThat(questionGroup.getTitle(), is(TITLE));
        assertThat(questionGroup.getState(), is(ACTIVE));
        assertSections(questionGroup.getSections());
        assertCreationDate(questionGroup.getDateOfCreation());
    }

    private void assertSections(List<Section> sections) {
        assertThat(sections, notNullValue());
        assertThat(sections.size(), is(1));
        Section section = sections.get(0);
        assertThat(section.getName(), is(SECTION_NAME));
        assertSectionQuestions(section.getQuestions());
    }

    private void assertSectionQuestions(List<SectionQuestion> sectionQuestions) {
        assertThat(sectionQuestions, notNullValue());
        assertThat(sectionQuestions.size(), is(1));
        SectionQuestion sectionQuestion = sectionQuestions.get(0);
        assertThat(sectionQuestion.getQuestion(), notNullValue());
        assertThat(sectionQuestion.getSection(), notNullValue());
        assertThat(sectionQuestion.isRequired(), is(true));
        assertThat(sectionQuestion.getSequenceNumber(), is(0));
    }

    private EventSource getEventSource(String event, String source) {
        return new EventSource(event, source, null);
    }

    private SectionDefinition getSectionDefinition(String name) {
        SectionDefinition section = new SectionDefinition();
        section.setName(name);
        section.addQuestion(new SectionQuestionDetail(12, true));
        return section;
    }

    @Test
    public void shouldMapQuestionGroupToQuestionGroupDetail() {
        QuestionGroup questionGroup = getQuestionGroup("Create", "Client", "S1", "S2");
        QuestionGroupDetail questionGroupDetail = questionnaireMapper.mapToQuestionGroupDetail(questionGroup);
        assertThat(questionGroupDetail, is(not(nullValue())));
        assertThat(questionGroupDetail.getTitle(), is(TITLE));
        List<SectionDefinition> sectionDefinitions = questionGroupDetail.getSectionDefinitions();
        assertThat(sectionDefinitions, is(not(nullValue())));
        assertThat(questionGroupDetail.getSectionDefinitions().size(), is(2));
        assertThat(questionGroupDetail.getSectionDefinitions().get(0).getName(), is("S1"));
        assertThat(questionGroupDetail.getSectionDefinitions().get(1).getName(), is("S2"));
        EventSource eventSource = questionGroupDetail.getEventSource();
        assertThat(eventSource, is(not(nullValue())));
        assertThat(eventSource.getEvent(), is("Create"));
        assertThat(eventSource.getSource(), is("Client"));
    }

    private QuestionGroup getQuestionGroup(String event, String source, String... sectionNames) {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setTitle(TITLE);
        questionGroup.setSections(getSections(sectionNames));
        questionGroup.setEventSources(getEventSources(event, source));
        return questionGroup;
    }

    private List<Section> getSections(String[] sectionNames) {
        List<Section> sections = new ArrayList<Section>();
        for (String sectionName : sectionNames) {
            sections.add(getSection(sectionName));
        }
        return sections;
    }

    private Section getSection(String sectionName) {
        Section section = new Section(sectionName);
        SectionQuestion sectionQuestion = new SectionQuestion();
        Question question = new Question();
        question.setShortName(sectionName);
        sectionQuestion.setQuestion(question);
        section.setQuestions(asList(sectionQuestion));
        return section;
    }

    private Set<EventSourceEntity> getEventSources(String event, String source) {
        EventSourceEntity eventSourceEntity = new EventSourceEntity();
        EventEntity eventEntity = new EventEntity();
        eventEntity.setName(event);
        eventSourceEntity.setEvent(eventEntity);
        EntityMaster entityMaster = new EntityMaster();
        entityMaster.setEntityType(source);
        eventSourceEntity.setSource(entityMaster);
        return Collections.singleton(eventSourceEntity);
    }

    @Test
    public void shouldMapQuestionGroupsToQuestionGroupDetails() {
        int countOfQuestions = 10;
        List<QuestionGroup> questionGroups = new ArrayList<QuestionGroup>();
        for (int i = 0; i < countOfQuestions; i++) {
            questionGroups.add(getQuestionGroup(TITLE + i, getSection(SECTION + i), getSection(SECTION + (i + 1))));
        }
        List<QuestionGroupDetail> questionGroupDetails = questionnaireMapper.mapToQuestionGroupDetails(questionGroups);
        assertThat(questionGroupDetails, is(notNullValue()));
        for (int i = 0; i < countOfQuestions; i++) {
            QuestionGroupDetail questionGroupDetail = questionGroupDetails.get(i);
            assertThat(questionGroupDetail.getTitle(), is(TITLE + i));
            SectionDefinition sectionDefinition1 = questionGroupDetail.getSectionDefinitions().get(0);
            assertThat(sectionDefinition1.getName(), is(SECTION + i));
            List<SectionQuestionDetail> questionDetails1 = sectionDefinition1.getQuestions();
            assertThat(questionDetails1.size(), is(1));
            assertThat(questionDetails1.get(0).getTitle(), is(SECTION + i));
            SectionDefinition sectionDefinition2 = questionGroupDetail.getSectionDefinitions().get(1);
            assertThat(sectionDefinition2.getName(), is(SECTION + (i + 1)));
            List<SectionQuestionDetail> questionDetails2 = sectionDefinition2.getQuestions();
            assertThat(questionDetails2.size(), is(1));
            assertThat(questionDetails2.get(0).getTitle(), is(SECTION + (i + 1)));
        }
    }

    @Test
    public void shouldMapToEventSources() {
        List<EventSourceEntity> events = getEventSourceEntities("Create", "Client", "Create Client");
        List<EventSource> eventSources = questionnaireMapper.mapToEventSources(events);
        assertThat(eventSources, is(not(nullValue())));
        assertThat(eventSources, new EventSourceMatcher("Create", "Client", "Create Client"));
    }

    private List<EventSourceEntity> getEventSourceEntities(String event, String source, String description) {
        List<EventSourceEntity> events = new ArrayList<EventSourceEntity>();
        EventSourceEntity eventSourceEntity = new EventSourceEntity();
        eventSourceEntity.setDescription(description);
        EventEntity eventEntity = new EventEntity();
        eventEntity.setName(event);
        eventSourceEntity.setEvent(eventEntity);
        EntityMaster entityMaster = new EntityMaster();
        entityMaster.setEntityType(source);
        eventSourceEntity.setSource(entityMaster);
        events.add(eventSourceEntity);
        return events;
    }

    private QuestionGroup getQuestionGroup(String title, Section... sections) {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setTitle(title);
        questionGroup.setSections(asList(sections));
        return questionGroup;
    }

    private void assertQuestionType(QuestionType questionType, AnswerType answerType) {
        QuestionDetail questionDetail = questionnaireMapper.mapToQuestionDetail(getQuestion(TITLE, answerType));
        assertThat(questionDetail.getType(), is(questionType));
    }

    private Question getQuestion(String title, AnswerType answerType) {
        return new Question(title, title, answerType);
    }

    private void assertQuestionDetail(QuestionDetail questionDetail, String title, QuestionType questionType) {
        assertThat(questionDetail.getText(), is(title));
        assertThat(questionDetail.getType(), is(questionType));
    }

    private void assertCreationDate(Date dateOfCreation) {
        Calendar creationDate = Calendar.getInstance();
        creationDate.setTime(dateOfCreation);
        Calendar currentDate = Calendar.getInstance();
        assertThat(creationDate.get(Calendar.DATE), is(currentDate.get(Calendar.DATE)));
        assertThat(creationDate.get(Calendar.MONTH), is(currentDate.get(Calendar.MONTH)));
        assertThat(creationDate.get(Calendar.YEAR), is(currentDate.get(Calendar.YEAR)));
    }
}
