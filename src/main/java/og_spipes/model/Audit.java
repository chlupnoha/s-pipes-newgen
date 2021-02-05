/**
 * Copyright (C) 2019 Czech Technical University in Prague
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package og_spipes.model;

import cz.cvut.kbss.jopa.model.annotations.CascadeType;
import cz.cvut.kbss.jopa.model.annotations.FetchType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;

import java.util.HashSet;
import java.util.Set;

@OWLClass(iri = Vocabulary.C_AUDIT)
public class Audit extends Event {

    @OWLObjectProperty(iri = Vocabulary.P_HAS_FINDING, cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Set<AuditFinding> findings;

    public void addFinding(AuditFinding finding) {
        if (findings == null) {
            this.findings = new HashSet<>();
        }
        findings.add(finding);
    }

    public Set<AuditFinding> getFindings() {
        return findings;
    }

    public void setFindings(Set<AuditFinding> findings) {
        this.findings = findings;
    }

    @Override
    public String toString() {
        return "Audit{" +
                "types=" + getTypes() +
                "findings=" + findings +
                '}';
    }
}
