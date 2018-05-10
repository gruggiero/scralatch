package com.github.madbrain.scralatch

object Example extends Scralatch {

  lutin("chat") {
    costume("costume1") image "/usr/share/scratch/Media/Costumes/Animals/cat1-a.gif" origine centre
    costume("costume2") image "/usr/share/scratch/Media/Costumes/Animals/cat1-b.gif" origine centre

    peutSeRetourner

    auDepart {
      vaA(-180, 0)
    }

    quandEstPressé(flecheDroite) {
      pointeVers(90)
      avance(5)
      costumeSuivant
      attend(0.1)
      avance(5)
      costumeSuivant
      attend(0.1)
    }

    quandEstPressé(flecheGauche) {
      pointeVers(-90)
      avance(5)
      costumeSuivant
      attend(0.1)
      avance(5)
      costumeSuivant
      attend(0.1)
    }
  }

}
