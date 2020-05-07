'use strict';

import * as express from 'express';
import movieService from '../services/movie_service';
const router = express.Router();

router.use('/movies', movieService);

/* GET home page. */
router.get('/',(req,res,next) => {
  res.render('index', {title: 'Express'});
});

export default router;