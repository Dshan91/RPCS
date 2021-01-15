/*
 * The MIT License
 *
 * Copyright 2021 buddhika.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lk.gov.health.phsp.bean;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import lk.gov.health.phsp.entity.Institution;
import lk.gov.health.phsp.entity.QueryComponent;
import lk.gov.health.phsp.entity.StoredQueryResult;
import lk.gov.health.phsp.facade.StoredQueryResultFacade;
import lk.gov.health.phsp.pojcs.Jpq;
import lk.gov.health.phsp.pojcs.Replaceable;

/**
 *
 * @author buddhika
 */
@Named
@SessionScoped
public class StoredQueryResultController implements Serializable {

    @EJB
    StoredQueryResultFacade facade;

    /**
     * Creates a new instance of StoredQueryResultController
     */
    public StoredQueryResultController() {
    }

    public Long findStoredLongValue(QueryComponent qc, Date fromDate, Date toDate, Institution institution) {
        System.out.println("findStoredLongValue");
        StoredQueryResult s;
        s = findStoredQueryResult(qc, fromDate, toDate, institution);
        System.out.println("s = " + s);
        if (s == null) {
            return null;
        }
        System.out.println("s.getLongValue() = " + s.getLongValue());
        return s.getLongValue();
    }
    
    public Long findStoredLongValue(QueryComponent qc, Date fromDate, Date toDate, Institution institution, Replaceable re, Jpq j) {
        StoredQueryResult s;
        s = findStoredQueryResult(qc, fromDate, toDate, institution);
        Long r;
        if (s == null) {
            r = null;
            j.setError(true);
            j.setMessage(j.getMessage() + "\n" + "No count for " + re.getQryCode() + " for " + institution.getName() + ".\n");
        } else {
            if (s.getLongValue() == null) {
                j.setError(true);
                j.setMessage(j.getMessage() + "\n" + "No count for " + re.getQryCode() + " for " + institution.getName() + ".\n");
                r = null;
            } else {
                r = s.getLongValue();
                j.setMessage(j.getMessage() + re.getQryCode() + " - " + r + "\n");
            }
        }
        return r;
    }

    public Long findStoredLongValue(QueryComponent qc, Date fromDate, Date toDate, List<Institution> institutions, Replaceable re, Jpq j) {
        Long insSum = 0L;
        for (Institution i : institutions) {
            Long ic = findStoredLongValue(qc, fromDate, toDate, i, re, j);
            if (ic != null) {
                insSum += ic;
            }
        }
        return insSum;
    }

    public StoredQueryResult findStoredQueryResult(QueryComponent qc, Date fromDate, Date toDate, Institution institution) {
        System.out.println("findStoredQueryResult");
        String j;
        Map m;
        System.out.println("qc.getName() = " + qc.getName());
        System.out.println("qc.getId() = " + qc.getId());
        m = new HashMap();
        m.put("fd", fromDate);
        m.put("td", toDate);
        m.put("ins", institution);
        m.put("qc", qc);
        j = "select s "
                + " from StoredQueryResult s "
                + " where s.institution=:ins "
                + " and s.resultFrom=:fd "
                + " and s.resultTo=:td "
                + " and s.queryComponent=:qc "
                + " order by s.id desc";
        System.out.println("m = " + m);
        System.out.println("j = " + j);
        return facade.findFirstByJpql(j, m);

    }

    public void saveValue(QueryComponent qc, Date fromDate, Date toDate, Institution institution, Long value) {
        StoredQueryResult s;
        s = findStoredQueryResult(qc, fromDate, toDate, institution);
        if (s == null) {
            s = new StoredQueryResult();
            s.setInstitution(institution);
            s.setResultFrom(fromDate);
            s.setResultTo(toDate);
            s.setQueryComponent(qc);
            s.setLongValue(value);
            facade.create(s);
        } else {
            s.setLongValue(value);
            facade.edit(s);
        }

    }

}