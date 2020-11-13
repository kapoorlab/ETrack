%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% Created by John Paul Minda, PhD			%%
%% Professor of Psychology					%%
%% The Brain and Mind Institute				%%
%% The University of Western Ontario		%%	
%% London, ON N6A 5C2						%%
%%											%%
%% Version 1.2								%%	
%% Feb 13, 2018								%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

\documentclass{article}
\usepackage{fullpage}

\renewcommand{\familydefault}{\sfdefault}
\usepackage[scaled=1]{helvet}
\usepackage[helvet]{sfmath}
\everymath={\sf}

\usepackage{parskip}
\usepackage[colorinlistoftodos]{todonotes}
\usepackage[colorlinks=true, allcolors=blue]{hyperref}

\title{Using ETrack}
\author{Varun Kapoor, Ozge Ozguc}
%\company{The University of Western Ontario}
\setcounter{tocdepth}{2}
\begin{document}
\maketitle
\tableofcontents

\section{Cells inside Embryo} 
In this manual we describe the python and Fiji based workflow to compute curvature of cells inside the mouse pre-implantation embryo. In this workflow we track the cells over time with curvature being computed locally for each time frame. We create an object consisting of local curvature, local intensity, perimeter, center of cell and boundary point information. Such an object is stored internally in the program and the information of the cell center is then used to link such objects over time. End result is information of local curvature variation of each cell tracked by the program. These outputs are written as kymographs (Perimeter vs time) with intensity of the kymograph being the local curvature measure. We also write this information in text files. Also stored are the local intensity kymographs (Perimeter over time). Essential components of this workflow are now described below.


\section{Segmenting Cells} 
For segmentation of various cells/tissues in biology UNET is a widely used neural network. As with other neural networks it requires training data which are dense labelled foreground-background set of images. We took 136 images of touching cells (about 6 touching cells minimum for each image). We created a random dataset of noisy input images and created ground truth labels for each of the timeframes using a freely available software called Lalbelme (python). The aim of our workflow is to do instance segmentation of cells. For this we use smarseeds algorithm which we developed using UNET, Sardist and watershedding techniques to achieve segmentations with minimal boundary reconstruction errors. We describe this segmentation technique in the next sections.

\subsection{UNET model}


\subsection{StarDist model}

\subsection{SmartCorrection}
In order to segment cells which are roundish stardist was developed. However when cells are large in size which is the case for mouse pre-implanantation embryos stardist has huge boundary reconstruction errors which makes the segmentations unusable as the variable we want to compute is the curvature. If we use the seeds/markers of stardist and try to do watershed segmentation on either the probability map or the distance map we suffer from "overflow" of watershed regions due to weak boundaries. Hence we developed a method without pre-processing to avoid the weak edge overflow problem and this forms a part of the smartseed segmentation environment, which uses stardist, UNET and watershed  along with smart correction to avoid "watershed region overflows" in 2D and 3D. Illustrating it in picture. in Fig.\ref{NotSmartSeeds} (top) the segmentations of stardist and smartseed without the smartcorrection and in Fig.\ref{SmartSeeds} (below) the segmentation with smartcorrection.

\begin{figure}[ht]
	\centering
\includegraphics[width=0.8\textwidth]{Uncorrected.png}

\caption{Smart Seed segmentation without smartcorrection}
\label{NotSmartSeeds}

\end{figure}

\begin{figure}[ht]
	\centering
\includegraphics[width=0.8\textwidth]{SmartCorrection.png}

\caption{Smart Seed segmentation with smartcorrection}
\label{SmartSeeds}

\end{figure}

\subsection{Napari Correction Tool}
The segmentations that we obtain using our smartseeds algorithm produces good results, however there may still be need for minor corrections and for that task we created a napari based correction tool. 

\section{Tracking Cells: ETrack}

ETrack is a Fiji tool which takes in the Raw image and instance segmentation of cells as input for computing the local curvature information



\subsection{Computing Curvature: Distance method}
Curvature or deformation of the boundary points can be computed by computing the distance of the center of the cell to the boundary points. Since we are dealing with roundish/ellipsoidal cell the center of mass, which is computed by averaging the X,Y co ordinates of the boundary points, will always lie in the center of the cell. Hence we can use that as a good reference point to measure the relative deformation of all the boundary points. The resulting kymograph hence has the units of distance (pixel units) as the intensity value.

\subsection{Computing Curvature: Circle Fits}
Distance method produces noisy results as the distance to the boundary point measured over time is not a space averaged result. A much nicer resolution for deformation measurement is provided by the circle fits method. In this method the user chooses the region size and the program uses the start, center and end point of the region to fit a circle and use the radius of the fitted circle as a measure of the local deformation. All the pixels in the region are assigned the curvature value equal to the inverse of the radius of the fitted circle. The region is then shifted by 1 pixel and a new circle is fitted. The curvature value for the boundary pixels is then averaged. This process is repeated till we cover all the points of the cell. In this way we are able to capture local information of the cell much better than the distance method and hence get less noisy results compared to the previous method. Also since the result is averaged over space for all the boundary points no point is preferred over other and the result obtained is invariant to the choice of the starting point for measuring the curvature.


\section{Program outputs} 

\subsection {Python kymograph analysis}

The analysis of tracks after using ETrack happens in Python. We are interested in characterizing the wave properties causing the cell deformation. The wave properties are characterized by its frequency, velocity and the dispersion relation. To obtain both we use Fourier transform techniques. For getting the frequency we take a cut along the perimeter axis of the kymograph and obtain a time series for that point. Fourier transforming the timeseries gives its frequency. We then do this for all the points along the perimeter to obtain a new perimeter versus frequency kymograph. If the input kymograph came from the distance method we will get noisier FFT kymograph as compared to the circle method. However for our case we get a single dominant peak in the FFT spectrum hence summing up the peaks in the kymograph along the y axis (perimeter) gives us a single dominant peak from both deformation measuring methods.

To get the velocity we make a cut along time and obtain space series analogus to the time series but now along the perimeter axis. We then do a FFT in space and get a momentum vs time kymograph. From this kymograph we extract the peak in momentum space. To characterize the wave velocity we assume that in first order the wave is a plane wave and its velocity is obtained by dividing the frequency peak with the momentum peak.

Next we confirm that the wave is indeed a plane wave by fitting straight lines to the original perimeter versus time kymograph, giving a more direct read out of the wave velocity which matches with the velocity we compute by doing FFT in space and time. This leads us to the conclusion of the wave travelling around the cells being a plane wave following a linear dispersion relation.

\end{document}
