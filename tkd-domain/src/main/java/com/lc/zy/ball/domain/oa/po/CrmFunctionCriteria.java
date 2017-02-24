package com.lc.zy.ball.domain.oa.po;

import java.util.ArrayList;
import java.util.List;

/**
* CrmFunctionCriteria 条件查询类.
* 
* @author sl
* @version v1.0
* @copy pet
* @date 2016-11-01 16:47:31
*/
public class CrmFunctionCriteria {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    protected Integer mysqlOffset;

    protected Integer mysqlLength;

    public CrmFunctionCriteria() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * @param mysqlOffset 
	 *            指定返回记录行的偏移量<br>
	 *            mysqlOffset= 5,mysqlLength=10;  // 检索记录行 6-15
     */
    public void setMysqlOffset(int mysqlOffset) {
        this.mysqlOffset=mysqlOffset;
    }

    /**
     * @param mysqlLength 
	 *            指定返回记录行的最大数目<br>
	 *            mysqlOffset= 5,mysqlLength=10;  // 检索记录行 6-15
     */
    public Integer getMysqlOffset() {
        return mysqlOffset;
    }

    /**
     * @param mysqlOffset 
	 *            指定返回记录行的偏移量<br>
	 *            mysqlOffset= 5,mysqlLength=10;  // 检索记录行 6-15
     */
    public void setMysqlLength(int mysqlLength) {
        this.mysqlLength=mysqlLength;
    }

    /**
     * @param mysqlLength 
	 *            指定返回记录行的最大数目<br>
	 *            mysqlOffset= 5,mysqlLength=10;  // 检索记录行 6-15
     */
    public Integer getMysqlLength() {
        return mysqlLength;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andFuncIdIsNull() {
            addCriterion("FUNC_ID is null");
            return (Criteria) this;
        }

        public Criteria andFuncIdIsNotNull() {
            addCriterion("FUNC_ID is not null");
            return (Criteria) this;
        }

        public Criteria andFuncIdEqualTo(String value) {
            addCriterion("FUNC_ID =", value, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdNotEqualTo(String value) {
            addCriterion("FUNC_ID <>", value, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdGreaterThan(String value) {
            addCriterion("FUNC_ID >", value, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdGreaterThanOrEqualTo(String value) {
            addCriterion("FUNC_ID >=", value, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdLessThan(String value) {
            addCriterion("FUNC_ID <", value, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdLessThanOrEqualTo(String value) {
            addCriterion("FUNC_ID <=", value, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdLike(String value) {
            addCriterion("FUNC_ID like", value, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdNotLike(String value) {
            addCriterion("FUNC_ID not like", value, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdIn(List<String> values) {
            addCriterion("FUNC_ID in", values, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdNotIn(List<String> values) {
            addCriterion("FUNC_ID not in", values, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdBetween(String value1, String value2) {
            addCriterion("FUNC_ID between", value1, value2, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncIdNotBetween(String value1, String value2) {
            addCriterion("FUNC_ID not between", value1, value2, "funcId");
            return (Criteria) this;
        }

        public Criteria andFuncNameIsNull() {
            addCriterion("FUNC_NAME is null");
            return (Criteria) this;
        }

        public Criteria andFuncNameIsNotNull() {
            addCriterion("FUNC_NAME is not null");
            return (Criteria) this;
        }

        public Criteria andFuncNameEqualTo(String value) {
            addCriterion("FUNC_NAME =", value, "funcName");
            return (Criteria) this;
        }

        public Criteria andFuncNameNotEqualTo(String value) {
            addCriterion("FUNC_NAME <>", value, "funcName");
            return (Criteria) this;
        }

        public Criteria andFuncNameGreaterThan(String value) {
            addCriterion("FUNC_NAME >", value, "funcName");
            return (Criteria) this;
        }

        public Criteria andFuncNameGreaterThanOrEqualTo(String value) {
            addCriterion("FUNC_NAME >=", value, "funcName");
            return (Criteria) this;
        }

        public Criteria andFuncNameLessThan(String value) {
            addCriterion("FUNC_NAME <", value, "funcName");
            return (Criteria) this;
        }

        public Criteria andFuncNameLessThanOrEqualTo(String value) {
            addCriterion("FUNC_NAME <=", value, "funcName");
            return (Criteria) this;
        }

        public Criteria andFuncNameLike(String value) {
            addCriterion("FUNC_NAME like", value, "funcName");
            return (Criteria) this;
        }

        public Criteria andFuncNameNotLike(String value) {
            addCriterion("FUNC_NAME not like", value, "funcName");
            return (Criteria) this;
        }

        public Criteria andFuncNameIn(List<String> values) {
            addCriterion("FUNC_NAME in", values, "funcName");
            return (Criteria) this;
        }

        public Criteria andFuncNameNotIn(List<String> values) {
            addCriterion("FUNC_NAME not in", values, "funcName");
            return (Criteria) this;
        }

        public Criteria andFuncNameBetween(String value1, String value2) {
            addCriterion("FUNC_NAME between", value1, value2, "funcName");
            return (Criteria) this;
        }

        public Criteria andFuncNameNotBetween(String value1, String value2) {
            addCriterion("FUNC_NAME not between", value1, value2, "funcName");
            return (Criteria) this;
        }

        public Criteria andParentIdIsNull() {
            addCriterion("PARENT_ID is null");
            return (Criteria) this;
        }

        public Criteria andParentIdIsNotNull() {
            addCriterion("PARENT_ID is not null");
            return (Criteria) this;
        }

        public Criteria andParentIdEqualTo(String value) {
            addCriterion("PARENT_ID =", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdNotEqualTo(String value) {
            addCriterion("PARENT_ID <>", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdGreaterThan(String value) {
            addCriterion("PARENT_ID >", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdGreaterThanOrEqualTo(String value) {
            addCriterion("PARENT_ID >=", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdLessThan(String value) {
            addCriterion("PARENT_ID <", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdLessThanOrEqualTo(String value) {
            addCriterion("PARENT_ID <=", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdLike(String value) {
            addCriterion("PARENT_ID like", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdNotLike(String value) {
            addCriterion("PARENT_ID not like", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdIn(List<String> values) {
            addCriterion("PARENT_ID in", values, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdNotIn(List<String> values) {
            addCriterion("PARENT_ID not in", values, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdBetween(String value1, String value2) {
            addCriterion("PARENT_ID between", value1, value2, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdNotBetween(String value1, String value2) {
            addCriterion("PARENT_ID not between", value1, value2, "parentId");
            return (Criteria) this;
        }

        public Criteria andActionIsNull() {
            addCriterion("ACTION is null");
            return (Criteria) this;
        }

        public Criteria andActionIsNotNull() {
            addCriterion("ACTION is not null");
            return (Criteria) this;
        }

        public Criteria andActionEqualTo(String value) {
            addCriterion("ACTION =", value, "action");
            return (Criteria) this;
        }

        public Criteria andActionNotEqualTo(String value) {
            addCriterion("ACTION <>", value, "action");
            return (Criteria) this;
        }

        public Criteria andActionGreaterThan(String value) {
            addCriterion("ACTION >", value, "action");
            return (Criteria) this;
        }

        public Criteria andActionGreaterThanOrEqualTo(String value) {
            addCriterion("ACTION >=", value, "action");
            return (Criteria) this;
        }

        public Criteria andActionLessThan(String value) {
            addCriterion("ACTION <", value, "action");
            return (Criteria) this;
        }

        public Criteria andActionLessThanOrEqualTo(String value) {
            addCriterion("ACTION <=", value, "action");
            return (Criteria) this;
        }

        public Criteria andActionLike(String value) {
            addCriterion("ACTION like", value, "action");
            return (Criteria) this;
        }

        public Criteria andActionNotLike(String value) {
            addCriterion("ACTION not like", value, "action");
            return (Criteria) this;
        }

        public Criteria andActionIn(List<String> values) {
            addCriterion("ACTION in", values, "action");
            return (Criteria) this;
        }

        public Criteria andActionNotIn(List<String> values) {
            addCriterion("ACTION not in", values, "action");
            return (Criteria) this;
        }

        public Criteria andActionBetween(String value1, String value2) {
            addCriterion("ACTION between", value1, value2, "action");
            return (Criteria) this;
        }

        public Criteria andActionNotBetween(String value1, String value2) {
            addCriterion("ACTION not between", value1, value2, "action");
            return (Criteria) this;
        }

        public Criteria andPermissionIsNull() {
            addCriterion("PERMISSION is null");
            return (Criteria) this;
        }

        public Criteria andPermissionIsNotNull() {
            addCriterion("PERMISSION is not null");
            return (Criteria) this;
        }

        public Criteria andPermissionEqualTo(String value) {
            addCriterion("PERMISSION =", value, "permission");
            return (Criteria) this;
        }

        public Criteria andPermissionNotEqualTo(String value) {
            addCriterion("PERMISSION <>", value, "permission");
            return (Criteria) this;
        }

        public Criteria andPermissionGreaterThan(String value) {
            addCriterion("PERMISSION >", value, "permission");
            return (Criteria) this;
        }

        public Criteria andPermissionGreaterThanOrEqualTo(String value) {
            addCriterion("PERMISSION >=", value, "permission");
            return (Criteria) this;
        }

        public Criteria andPermissionLessThan(String value) {
            addCriterion("PERMISSION <", value, "permission");
            return (Criteria) this;
        }

        public Criteria andPermissionLessThanOrEqualTo(String value) {
            addCriterion("PERMISSION <=", value, "permission");
            return (Criteria) this;
        }

        public Criteria andPermissionLike(String value) {
            addCriterion("PERMISSION like", value, "permission");
            return (Criteria) this;
        }

        public Criteria andPermissionNotLike(String value) {
            addCriterion("PERMISSION not like", value, "permission");
            return (Criteria) this;
        }

        public Criteria andPermissionIn(List<String> values) {
            addCriterion("PERMISSION in", values, "permission");
            return (Criteria) this;
        }

        public Criteria andPermissionNotIn(List<String> values) {
            addCriterion("PERMISSION not in", values, "permission");
            return (Criteria) this;
        }

        public Criteria andPermissionBetween(String value1, String value2) {
            addCriterion("PERMISSION between", value1, value2, "permission");
            return (Criteria) this;
        }

        public Criteria andPermissionNotBetween(String value1, String value2) {
            addCriterion("PERMISSION not between", value1, value2, "permission");
            return (Criteria) this;
        }

        public Criteria andIsMenuIsNull() {
            addCriterion("IS_MENU is null");
            return (Criteria) this;
        }

        public Criteria andIsMenuIsNotNull() {
            addCriterion("IS_MENU is not null");
            return (Criteria) this;
        }

        public Criteria andIsMenuEqualTo(Short value) {
            addCriterion("IS_MENU =", value, "isMenu");
            return (Criteria) this;
        }

        public Criteria andIsMenuNotEqualTo(Short value) {
            addCriterion("IS_MENU <>", value, "isMenu");
            return (Criteria) this;
        }

        public Criteria andIsMenuGreaterThan(Short value) {
            addCriterion("IS_MENU >", value, "isMenu");
            return (Criteria) this;
        }

        public Criteria andIsMenuGreaterThanOrEqualTo(Short value) {
            addCriterion("IS_MENU >=", value, "isMenu");
            return (Criteria) this;
        }

        public Criteria andIsMenuLessThan(Short value) {
            addCriterion("IS_MENU <", value, "isMenu");
            return (Criteria) this;
        }

        public Criteria andIsMenuLessThanOrEqualTo(Short value) {
            addCriterion("IS_MENU <=", value, "isMenu");
            return (Criteria) this;
        }

        public Criteria andIsMenuIn(List<Short> values) {
            addCriterion("IS_MENU in", values, "isMenu");
            return (Criteria) this;
        }

        public Criteria andIsMenuNotIn(List<Short> values) {
            addCriterion("IS_MENU not in", values, "isMenu");
            return (Criteria) this;
        }

        public Criteria andIsMenuBetween(Short value1, Short value2) {
            addCriterion("IS_MENU between", value1, value2, "isMenu");
            return (Criteria) this;
        }

        public Criteria andIsMenuNotBetween(Short value1, Short value2) {
            addCriterion("IS_MENU not between", value1, value2, "isMenu");
            return (Criteria) this;
        }

        public Criteria andSeqNumIsNull() {
            addCriterion("SEQ_NUM is null");
            return (Criteria) this;
        }

        public Criteria andSeqNumIsNotNull() {
            addCriterion("SEQ_NUM is not null");
            return (Criteria) this;
        }

        public Criteria andSeqNumEqualTo(Integer value) {
            addCriterion("SEQ_NUM =", value, "seqNum");
            return (Criteria) this;
        }

        public Criteria andSeqNumNotEqualTo(Integer value) {
            addCriterion("SEQ_NUM <>", value, "seqNum");
            return (Criteria) this;
        }

        public Criteria andSeqNumGreaterThan(Integer value) {
            addCriterion("SEQ_NUM >", value, "seqNum");
            return (Criteria) this;
        }

        public Criteria andSeqNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("SEQ_NUM >=", value, "seqNum");
            return (Criteria) this;
        }

        public Criteria andSeqNumLessThan(Integer value) {
            addCriterion("SEQ_NUM <", value, "seqNum");
            return (Criteria) this;
        }

        public Criteria andSeqNumLessThanOrEqualTo(Integer value) {
            addCriterion("SEQ_NUM <=", value, "seqNum");
            return (Criteria) this;
        }

        public Criteria andSeqNumIn(List<Integer> values) {
            addCriterion("SEQ_NUM in", values, "seqNum");
            return (Criteria) this;
        }

        public Criteria andSeqNumNotIn(List<Integer> values) {
            addCriterion("SEQ_NUM not in", values, "seqNum");
            return (Criteria) this;
        }

        public Criteria andSeqNumBetween(Integer value1, Integer value2) {
            addCriterion("SEQ_NUM between", value1, value2, "seqNum");
            return (Criteria) this;
        }

        public Criteria andSeqNumNotBetween(Integer value1, Integer value2) {
            addCriterion("SEQ_NUM not between", value1, value2, "seqNum");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}