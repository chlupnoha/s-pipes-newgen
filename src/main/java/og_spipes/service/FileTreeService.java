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
package og_spipes.service;

import og_spipes.model.filetree.FileTree;
import og_spipes.model.filetree.Leaf;
import og_spipes.model.filetree.SubTree;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileTreeService {

    @Transactional
    public SubTree getTtlFileTree(File file) {
        List<FileTree> fileTrees = new ArrayList<>();

        for(File f : file.listFiles()){
            if (f.isFile() && f.getName().endsWith(".ttl")) {
                fileTrees.add(new Leaf(f.getAbsolutePath(), f.getName()));
            } else if (f.isDirectory()) {
                fileTrees.add(getTtlFileTree(f));
            }
        }

        return new SubTree(fileTrees, file.getName());
    }

}
